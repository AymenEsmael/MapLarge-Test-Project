let currentPath = '/'

//DOM Elem
const folderContainer = document.getElementById('folderContainer');
const infoContainer = document.getElementById('infoContainer');
const backButton = document.getElementById('backButton');

async function fetchFolderContents(path, pushToHistory=true) {
    // console.log(`Fetching contents for path: ${path}`);
    try{
        //calls the c# end point to get the data of the current directory
        const response = await fetch(`/api/directory?path=${encodeURIComponent(path)}`);
        const data = await response.json();
        if (!response.ok) {
            console.error("Backend Error:", data.error);
            alert("Failed to load directory: " + (data.error || "Unknown error"));
            return; 
        }

        currentPath = path;//set the current path to the new path

        if (pushToHistory){
            const newurl = currentPath === '/' ? '/' : `/?path=${encodeURIComponent(currentPath)}`;
            window.history.pushState({savedPath: currentPath}, "", newurl);
        }
        
        //render the UI with the new contents and stats
        // console.log('Fetched data:', data); //for debugging
        renderContents(data.contents);
        updateStats(data.stats)

        backButton.disabled = currentPath === '/';//disable back button if at root
    }
    catch (error) {
        console.error('Error fetching folder contents:', error);
    }

}

// Fetch the initial folder contents when the page loads
document.addEventListener('DOMContentLoaded', () => {
    const urlParam = new URLSearchParams(window.location.search);
    const searchQuery = urlParam.get('search');
    const startPath = urlParam.get('path') || '/'
    if (searchQuery){
        searchFiles(searchQuery,false);
    }
    else{
        fetchFolderContents(startPath, false);
    } 
});