using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    [Route("api/directory")]
    // [Route("api/file")]
    public class DirectoryController : ControllerBase
    {
        
    
    [HttpGet]
    public IActionResult GetDirectoryContents([FromQuery] string path = "/")
        {
            // Console.WriteLine($"Received request for path: {path}");
            try
            {
                
                var querypath = string.IsNullOrEmpty(path) ? "/" : path; // Default to root if no path is provided
                var relativePath = querypath.TrimStart('/'); // Remove leading slash for relative path
                var fullPath = Path.Combine(Program.HomeDirectory, relativePath);
                
                var dirInfo = new DirectoryInfo(fullPath);
                var directories = dirInfo.GetDirectories();//get the directories in the current path
                var files = dirInfo.GetFiles();//get the files in the current path

                //clean up the directories and files to remove hidden files and folders
                var vaildFiles = files.Where(f => !f.Name.StartsWith("."));
                var vaildDirectories = directories.Where(d => !d.Name.StartsWith("."));
                // Console.WriteLine($"Files found: {string.Join(", ", vaildFiles.Select(f => f.Name))}");

                long totalSize = vaildFiles.Sum(f => f.Length);//sum the size of all files in the current path
                //Console.WriteLine($"Folders found: {vaildDirectories.Count()}, Files found: {vaildFiles.Count()}, Total size: {totalSize} bytes");

                //prepare the result to be returned to the client
                var result = vaildDirectories.Select(d => new
                {
                    Name = d.Name,
                    Type = "folder"
                }).Concat(vaildFiles.Select(f => new
                {
                    Name = f.Name,
                    Type = "file",
                })).ToList();

                var stat = new
                {
                    totalFolders = vaildDirectories.Count(),
                    totalFiles = vaildFiles.Count(),
                    totalSize = ByteFormat(totalSize)
                };

                return Ok(new
                {
                    path = querypath,
                    stats = stat,
                    contents = result   
                });

            }
            catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }

    //Helper function that calculates the size of the files
    public string ByteFormat(long bytes)
        {
            // Console.WriteLine($"Formatting {bytes} bytes");
            if (bytes == 0) return "0 Bytes";

            string[] sizes = {"Bytes", "KB", "MB", "GB", "TB"};

            int suffix = (int)Math.Floor(Math.Log(bytes) / Math.Log(1024));

            if (suffix >= sizes.Length) suffix = sizes.Length - 1;

            // Console.WriteLine($"Suffix index: {suffix}, Size label: {sizes[suffix ]}");
            return Math.Round(bytes / Math.Pow(1024, suffix), 2) + " " + sizes[suffix];
        }
    }
}