using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;

namespace TestProject.Controllers
{
    [ApiController]
    public class UploadController : ControllerBase
    {
        [HttpPost("/upload")]
        public async Task<IActionResult> UploadFiles(List<IFormFile> uploaded_file)
        {
            try{
                if (uploaded_file == null || uploaded_file.Count == 0)
                {
                    return BadRequest("No files were uploaded.");
                }

                var srcpath = Path.Combine(Directory.GetCurrentDirectory(), "storage");
                foreach (var file in uploaded_file)
                {
                    var dstpath = Path.Combine(srcpath, file.FileName);
                    using (var stream = new FileStream(dstpath, FileMode.Create))
                    {
                        await file.CopyToAsync(stream);
                    }
                    
                }
                return Redirect("/");
            }catch(Exception ex){
                return StatusCode(500, $"Internal server error: {ex.Message}");
            }
        }
    }
}