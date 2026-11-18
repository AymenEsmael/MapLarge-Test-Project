using Microsoft.AspNetCore.Mvc;
using System;
using System.IO;

namespace TestProject.Controllers
{
    [ApiController]
    public class DeleteController : ControllerBase
    {
        [HttpDelete("api/delete")]
        public IActionResult DeleteFile([FromQuery] string path)
        {
            Console.WriteLine(path);
            try
            {
                if (string.IsNullOrEmpty(path))
                {
                    return BadRequest(new {Message = "Path is empty"});
                }
                var relativePath = path.TrimStart('/');
                var fullPath = Path.Combine(Directory.GetCurrentDirectory(),"storage", relativePath);

                System.IO.File.Delete(fullPath);
                Console.WriteLine("File has been deleted");
                return Ok(new {Message = "Success"});
            }catch(Exception e)
            {
                return BadRequest(new {error = e.Message});
            }
            
        }
    }
}