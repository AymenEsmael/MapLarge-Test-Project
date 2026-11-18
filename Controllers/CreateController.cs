using Microsoft.AspNetCore.Mvc;
using System;
using System.IO;

namespace TestProject.Controllers
{
    [ApiController]
    public class CreateController : ControllerBase
    {
        [HttpPost("/create")]
        public IActionResult Create([FromForm] string path, [FromForm] string type)
        {
            if (string.IsNullOrEmpty(path) || string.IsNullOrEmpty(type))
            {
                return BadRequest(new {error = "Folder/File name is empty"});
            }
            try
            {
                var querypath = Path.Combine(Directory.GetCurrentDirectory(),"storage");
                var relativePath = path.TrimStart('/');
                var fullPath = Path.Combine(querypath, relativePath);

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