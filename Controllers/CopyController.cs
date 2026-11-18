using Microsoft.AspNetCore.Mvc;
using System;
using System.IO;

namespace TestProject.Controllers
{
    [ApiController]
    public class CopyController : ControllerBase
    {
        [HttpPost("/api/copy")]
        public IActionResult CopyFile([FromQuery] string sourcePath, [FromQuery] string destPath)
        {
            if (string.IsNullOrEmpty(sourcePath) || string.IsNullOrEmpty(destPath))
            {
                return BadRequest(new { error = "Both sourcePath and destPath are required" });
            }

            try
            {
                var relativeSource = sourcePath.TrimStart('/');
                var relativeDest = destPath.TrimStart('/');
                
                var fullSource = Path.Combine(Directory.GetCurrentDirectory(), "storage", relativeSource);
                var fullDest = Path.Combine(Directory.GetCurrentDirectory(), "storage", relativeDest);

                

                //COPY. true allows it to overwrite if a file with the same name exists
                System.IO.File.Copy(fullSource, fullDest, overwrite: true);

                return Ok(new { message = "Success" });
            }
            catch (Exception e)
            {
                return BadRequest(new { error = e.Message });
            }
        }
    }
}