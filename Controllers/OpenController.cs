using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.StaticFiles;
using System;
using System.IO;

namespace TestProject.Controllers
{
    [ApiController]
    public class OpenController : ControllerBase
    {
        [HttpGet("/api/open")]
        public IActionResult OpenFile([FromQuery] string path)
        {
            if (string.IsNullOrEmpty(path))
            {
                return BadRequest(new {message="the input is empty"});
            }
            try{


                var relativePath = path.TrimStart('/');
                var fullPath = Path.Combine(Directory.GetCurrentDirectory(),"storage", relativePath);
                // string contentType = "application/octet-stream";
                // string extension = Path.GetExtension(fullPath).ToLower();
                    
                var provider = new FileExtensionContentTypeProvider();
                    
                    // Get the exact type. If it fails (unknown extension), default to text/plain
                if (!provider.TryGetContentType(fullPath, out string contentType))
                {
                    contentType = "text/plain"; 
                }

                return PhysicalFile(fullPath, contentType);
            }catch(Exception e)
            {
                return BadRequest(new {error = e.Message});
            }






        }
    }
}