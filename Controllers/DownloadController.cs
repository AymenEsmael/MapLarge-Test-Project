using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    [Route("api/file")]
    public class DownloadController : ControllerBase
    {
        [HttpGet]
        public async Task<IActionResult> DownloadFile([FromQuery] string path)
        {
            // Console.WriteLine($"Initiating file download for path: {path}");
            try
            {

                var querypath = string.IsNullOrEmpty(path) ? "/" : path; // Default to root if no path is provided
                var relativePath = querypath.TrimStart('/'); // Remove leading slash for relative path
                var fullPath = Path.Combine(Program.HomeDirectory, relativePath);

                var stream = System.IO.File.OpenRead(fullPath);
                // Console.WriteLine($"Preparing to download file: {fullPath}");
                //downloads the file to the user download file
                return File(stream, "application/octet-stream", Path.GetFileName(fullPath));
                
            }catch (Exception ex)
            {
                return BadRequest(new { error = ex.Message });
            }
        }
    }
}