
using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    [Route("/api/search")]

    public class SearchController : ControllerBase
    {
        [HttpGet]
        public IActionResult SearchFiles([FromQuery] string query)
        {

            if (string.IsNullOrWhiteSpace(query)) return BadRequest("Search can not be empty");
            try
            {
                //to recalculate the size of the files as a result of the search
                DirectoryController dController = new DirectoryController();

                var searchPath = Program.HomeDirectory; // Start searching from the storage directory
                string[] matchingFiles = Directory.GetFiles(searchPath, $"*{query}*", SearchOption.AllDirectories).Where(f => !Path.GetFileName(f).StartsWith(".")).ToArray();
                string[] matchingDirectories = Directory.GetDirectories(searchPath, $"*{query}*", SearchOption.AllDirectories).Where(d => !Path.GetFileName(d).StartsWith(".")).ToArray();

                //gets the size of the files
                long totalSize = matchingFiles.Sum(f => new FileInfo(f).Length);
                
                var result = matchingFiles.Select(f => new
                {
                    Name = Path.GetFileName(f),
                    Type = "file",
                    path = "/" + Path.GetRelativePath(searchPath,f).Replace("\\", "/")
                }).Concat(matchingDirectories.Select(d => new
                {
                    Name = Path.GetFileName(d),
                    Type = "folder",
                    path =  "/" + Path.GetRelativePath(searchPath,d).Replace("\\","/")
                })).ToList();

                // Console.WriteLine("SEARCH CONTROLLER: " +   result);

                //prep the json to return
                var stat = new
                {
                    totalFolders = matchingDirectories.Length,
                    totalFiles = matchingFiles.Length,
                    totalSize = dController.ByteFormat(totalSize)
                };

                return Ok(new
                {
                    path = "/",
                    stats = stat,
                    contents = result
                });
            }catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
            
        }
    }

}