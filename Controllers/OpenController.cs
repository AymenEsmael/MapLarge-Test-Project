using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.StaticFiles;


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
                var fullPath = Path.Combine(Program.HomeDirectory, relativePath);
                
                //gets the extension type of the file to be able to view 
                var provider = new FileExtensionContentTypeProvider();
                    
                    //get the exact type. If it fails, default to text/plain
                if (!provider.TryGetContentType(fullPath, out string? contentType))
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