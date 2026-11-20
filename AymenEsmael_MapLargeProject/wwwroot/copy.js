let clipboardFilePath = null;
let clipboardFileName = null;
let clipboardAction = null; //copy or move

document.getElementById('btnCopy').addEventListener('click', () => {
    //Save to virtual clipboard
    clipboardFilePath = currentSelectedFilePath;
    clipboardFileName = currentSelectedFileName;
    clipboardAction = 'copy';
    
    //make it pasteable
    const pasteBtn = document.getElementById('btnPaste');
    if (pasteBtn) {
        pasteBtn.style.display = 'inline-block';
        pasteBtn.textContent = `Paste ${clipboardFileName} Here`;
    }
    
    fileModal.close();
});

document.getElementById('btnMove').addEventListener('click', () => {
    //Save to virtual clipboard
    clipboardFilePath = currentSelectedFilePath;
    clipboardFileName = currentSelectedFileName;
    clipboardAction = 'move';
    
    //make it pasteable
    const pasteBtn = document.getElementById('btnPaste');
    if (pasteBtn) {
        pasteBtn.style.display = 'inline-block';
        pasteBtn.textContent = `Paste ${clipboardFileName} Here`;
    }
    fileModal.close();
});

document.getElementById('btnPaste').addEventListener('click', async () => {
    // Safety check
    if (!clipboardFilePath || !clipboardFileName) return;

    // Construct the dst path
    const destPath = currentPath === '/' 
        ? `/${clipboardFileName}` 
        : `${currentPath}/${clipboardFileName}`;

    try {
        console.log(`Pasting from ${clipboardFilePath} to ${destPath}`);
        
        // Call the C# CopyController
        let response = null;
        if (clipboardAction === 'copy'){
            response = await fetch(`/api/copy?sourcePath=${encodeURIComponent(clipboardFilePath)}&destPath=${encodeURIComponent(destPath)}`, {
                method: 'POST'
            });
        }
        else if (clipboardAction === 'move'){
            response = await fetch (`/api/move?sourcePath=${encodeURIComponent(clipboardFilePath)}&destPath=${encodeURIComponent(destPath)}`, {
                method: 'POST'
            });
        }
        
        const data = await response.json();
        
        if (response.ok) {
            //hide the paste button, clear the clipboard, and refresh the folder
            document.getElementById('btnPaste').style.display = 'none';
            clipboardFilePath = null;
            clipboardFileName = null;
            
            fetchFolderContents(currentPath); 
        } else {
            alert("Error pasting file: " + (data.error || data.message));
        }
    } catch (err) {
        console.error("Paste failed:", err.message);
    }
});