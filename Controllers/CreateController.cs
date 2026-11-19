using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    public class CreateController : ControllerBase
    {
        [HttpPost("/api/create")]
        public IActionResult Create([FromForm] string path, [FromForm] string type)
        {
            if (string.IsNullOrEmpty(path) || string.IsNullOrEmpty(type))
            {
                return BadRequest(new {error = "Folder/File name is empty"});
            }
            try
            {
                var relativePath = path.TrimStart('/');
                var fullPath = Path.Combine(Program.HomeDirectory, relativePath);

                //calls the apriorate end point based on the type we are creating
                if (type.Equals("folder"))
                {
                    Directory.CreateDirectory(fullPath);
                    return Ok(new {message = "New folder has been created"});
                }
                else
                {
                    using var fs = System.IO.File.Create(fullPath);
                    return Ok(new {message = "New File have been created"});
                }
            }catch(Exception ex)
            {
                return BadRequest(new {error = ex.Message});
            }



        }
    }
}