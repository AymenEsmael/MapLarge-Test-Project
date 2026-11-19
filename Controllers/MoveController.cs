using Microsoft.AspNetCore.Mvc;


namespace TestProject.Controllers
{
    [ApiController]
    public class MoveController : ControllerBase
    {
        [HttpPost("/api/move")]
        public IActionResult MoveFile([FromQuery] string sourcePath, [FromQuery] string destPath)
        {
            if (string.IsNullOrEmpty(sourcePath) || string.IsNullOrEmpty(destPath))
            {
                return BadRequest(new { error = "Both sourcePath and destPath are required" });
            }

            try
            {
                //prep the src and dst path
                var relativeSource = sourcePath.TrimStart('/');
                var relativeDest = destPath.TrimStart('/');
                
                var fullSource = Path.Combine(Program.HomeDirectory, relativeSource);
                var fullDest = Path.Combine(Program.HomeDirectory, relativeDest);

                

                //COPY. true overrides if a file already exists
                System.IO.File.Move(fullSource, fullDest, overwrite: true);

                return Ok(new { message = "Success" });
            }
            catch (Exception e)
            {
                return BadRequest(new { error = e.Message });
            }
        }
    }
}