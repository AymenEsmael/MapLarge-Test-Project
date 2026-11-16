
using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    [Route("/search")]

    public class SearchController : ControllerBase
    {
        [HttpGet]
        public IActionResult SearchFiles([FromQuery] string query)
        {

            if (string.IsNullOrWhiteSpace(query)) return BadRequest("Search can not be empty");
            try
            {
                DirectoryController dController = new DirectoryController();
                var searchPath = Path.Combine(Directory.GetCurrentDirectory(), "storage"); // Start searching from the storage directory
                string[] matchingFiles = Directory.GetFiles(searchPath, $"*{query}*", SearchOption.AllDirectories);
                string[] matchingDirectories = Directory.GetDirectories(searchPath, $"*{query}*", SearchOption.AllDirectories);

                long totalSize = matchingFiles.Sum(f => new FileInfo(f).Length);
                
                var result = matchingFiles.Select(f => new
                {
                    Name = Path.GetFileName(f),
                    Type = "file",
                    path = "/" + Path.GetRelativePath(searchPath,f)
                }).Concat(matchingDirectories.Select(d => new
                {
                    Name = Path.GetFileName(d),
                    Type = "folder",
                    path =  "/" + Path.GetRelativePath(searchPath,d)
                })).ToList();

                Console.WriteLine("SEARCH CONTROLLER: " +   result);

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