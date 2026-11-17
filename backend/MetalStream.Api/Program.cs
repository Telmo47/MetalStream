using System.Security.Cryptography;
using System.Text;
using Microsoft.AspNetCore.StaticFiles;

var builder = WebApplication.CreateBuilder(args);

// Configurar CORS para permitir que a app Android / Web aceda à API sem restrições
builder.Services.AddCors(options =>
{
    options.AddDefaultPolicy(policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

var app = builder.Build();

app.UseCors();

var contentTypeProvider = new FileExtensionContentTypeProvider();

// Caminho para a pasta onde os ficheiros MP3 estão guardados
var musicFolderPath = Path.Combine(app.Environment.ContentRootPath, "Music");

// Garantir que a pasta existe
if (!Directory.Exists(musicFolderPath))
{
    Directory.CreateDirectory(musicFolderPath);
}

// Endpoint de saúde / boas-vindas
app.MapGet("/", () => new
{
    service = "MetalStream API",
    status = "running",
    version = "1.0.0",
    docs = "/api/songs"
});

// GET & HEAD /api/songs -> Lista todas as faixas disponíveis
app.MapMethods("/api/songs", new[] { "GET", "HEAD" }, (HttpRequest request) =>
{
    var supportedExtensions = new[] { ".mp3", ".wav", ".flac", ".m4a" };
    var files = Directory.EnumerateFiles(musicFolderPath, "*.*", SearchOption.TopDirectoryOnly)
        .Where(f => supportedExtensions.Contains(Path.GetExtension(f).ToLowerInvariant()))
        .ToList();

    var songs = new List<SongDto>();

    foreach (var filePath in files)
    {
        var fileName = Path.GetFileName(filePath);
        var songId = GenerateSongId(fileName);

        string title = Path.GetFileNameWithoutExtension(filePath);
        string artist = "MetalStream Artist";
        string album = "Unknown Album";
        double durationSeconds = 0;

        try
        {
            using var tagFile = TagLib.File.Create(filePath);
            if (!string.IsNullOrWhiteSpace(tagFile.Tag.Title))
                title = tagFile.Tag.Title;

            if (!string.IsNullOrWhiteSpace(tagFile.Tag.FirstPerformer))
                artist = tagFile.Tag.FirstPerformer;
            else if (!string.IsNullOrWhiteSpace(tagFile.Tag.FirstAlbumArtist))
                artist = tagFile.Tag.FirstAlbumArtist;

            if (!string.IsNullOrWhiteSpace(tagFile.Tag.Album))
                album = tagFile.Tag.Album;

            durationSeconds = Math.Round(tagFile.Properties.Duration.TotalSeconds, 2);
        }
        catch
        {
            // Se o ficheiro não tiver tags ID3 válidas, fazemos fallback com o nome do ficheiro
            // Exemplo: "01 - Master Of Puppets" ou "Metallica - Master Of Puppets"
            var parts = title.Split('-', 2);
            if (parts.Length == 2)
            {
                artist = parts[0].Trim();
                title = parts[1].Trim();
            }
        }

        var streamUrl = $"{request.Scheme}://{request.Host}/api/songs/{songId}/stream";

        songs.Add(new SongDto(
            Id: songId,
            Title: title,
            Artist: artist,
            Album: album,
            DurationSeconds: durationSeconds,
            FileName: fileName,
            StreamUrl: streamUrl
        ));
    }

    return Results.Ok(songs);
});

// GET & HEAD /api/songs/{id}/stream -> Streaming de áudio com suporte a Range Requests (HTTP 206)
app.MapMethods("/api/songs/{id}/stream", new[] { "GET", "HEAD" }, (string id) =>
{
    var supportedExtensions = new[] { ".mp3", ".wav", ".flac", ".m4a" };
    var matchingFile = Directory.EnumerateFiles(musicFolderPath, "*.*", SearchOption.TopDirectoryOnly)
        .FirstOrDefault(f => 
            supportedExtensions.Contains(Path.GetExtension(f).ToLowerInvariant()) &&
            GenerateSongId(Path.GetFileName(f)) == id);

    if (matchingFile == null || !File.Exists(matchingFile))
    {
        return Results.NotFound(new { error = $"Música com id '{id}' não encontrada." });
    }

    if (!contentTypeProvider.TryGetContentType(matchingFile, out var contentType))
    {
        contentType = "application/octet-stream";
    }

    // enableRangeProcessing: true é FUNDAMENTAL para streaming de música.
    // Permite que o player (ex: ExoPlayer no Android) salte para a frente/trás (seek)
    // e faça buffer parcial usando HTTP 206 Partial Content.
    return Results.File(matchingFile, contentType: contentType, enableRangeProcessing: true);
});

app.Run();

// Gera um ID estável e seguro para URL com base no nome do ficheiro
static string GenerateSongId(string fileName)
{
    var bytes = MD5.HashData(Encoding.UTF8.GetBytes(fileName));
    return Convert.ToHexString(bytes).ToLowerInvariant()[..12];
}

// Modelo de resposta JSON para cada música
public record SongDto(
    string Id,
    string Title,
    string Artist,
    string Album,
    double DurationSeconds,
    string FileName,
    string StreamUrl
);
