namespace TestProject {
    public class Program {
        public static string HomeDirectory {get; private set;} = string.Empty;
        public static void Main(string[] args) {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.
            //creates and sets a home directory to place each folder/file
            var configuredDir = builder.Configuration["HomeDirectory"] ?? "storage";
            HomeDirectory = Path.GetFullPath(configuredDir, Directory.GetCurrentDirectory());
            Directory.CreateDirectory(HomeDirectory);

            builder.Services.AddControllers(); 

            var app = builder.Build();

            // Configure the HTTP request pipeline.

            app.UseHttpsRedirection();

            app.UseDefaultFiles(); // Serve index.html as the default file

            app.UseStaticFiles();

            app.MapControllers();

            app.Run();
        }
    }
}