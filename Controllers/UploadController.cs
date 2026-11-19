using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    public class UploadController : ControllerBase
    {
        [HttpPost("/api/upload")]
        [DisableRequestSizeLimit]//Disables the small limit for upload
        [RequestFormLimits(MultipartBodyLengthLimit = int.MaxValue)]//sets new limit for upload
        public async Task<IActionResult> UploadFiles(List<IFormFile> uploaded_file, [FromForm] string path)
        {
            try{
                if (uploaded_file == null || uploaded_file.Count == 0)
                {
                    return BadRequest("No files were uploaded.");
                }

                var querypath = string.IsNullOrEmpty(path) ? "/" : path; // Default to root if no path is provided
                var relativePath = querypath.TrimStart('/'); // Remove leading slash for relative path
                var srcpath = Path.Combine(Program.HomeDirectory, relativePath);

                //goes through the uploaded files
                foreach (var file in uploaded_file)
                {
                    var dstpath = Path.Combine(srcpath, file.FileName);
                    //If the file already exists it sileintly skips it
                    if (System.IO.File.Exists(dstpath))
                    {
                        continue; // Skip the file if it already exists
                    }
                    // Console.WriteLine($"Uploading file: {file.FileName} to {dstpath}");
                    //uploads the file to the dst 
                    using (var stream = new FileStream(dstpath, FileMode.Create))
                    {
                        await file.CopyToAsync(stream);
                    }
                }
                return Ok("Files uploaded successfully.");
            }catch(Exception ex){
                return StatusCode(500, $"Internal server error: {ex.Message}");
            }
        }
    }
}