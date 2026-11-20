using Microsoft.AspNetCore.Mvc;

namespace TestProject.Controllers
{
    [ApiController]
    public class DeleteController : ControllerBase
    {
        [HttpDelete("api/delete")]
        public IActionResult DeleteFile([FromQuery] string path, [FromQuery] string type)
        {
            Console.WriteLine(path);
            try
            {
                if (string.IsNullOrEmpty(path) || string.IsNullOrEmpty(type))
                {
                    return BadRequest(new {Message = "Path and type are required"});
                }
                var relativePath = path.TrimStart('/');
                var fullPath = Path.Combine(Program.HomeDirectory, relativePath);

                //calls the apporopirate end point based on the type we are deleting
                if (string.Equals(type, "file"))
                {
                    System.IO.File.Delete(fullPath);
                    Console.WriteLine("File has been deleted");
                    return Ok(new {Message = "Success"});
                }
                else if (string.Equals(type, "folder"))
                {
                    Directory.Delete(fullPath,true);//true, deletes the file and the subdir and the files
                    return Ok(new{Message = "Success"});
                }
                return BadRequest(new{error = "Invalid type. Expected file or folder"});

                
            }catch(Exception e)
            {
                return BadRequest(new {error = e.Message});
            }
            
        }
    }
}