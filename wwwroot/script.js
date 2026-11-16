document.addEventListener('DOMContentLoaded', () => {
    const folderButton = document.getElementById('folderButton');

    if (folderButton) {
        folderButton.addEventListener('click', () => {
            console.log('Folder button clicked!');
        });
    } else {
        console.error('Could not find element with ID "folderButton".');
    }
});

document.addEventListener('DOMContentLoaded', () => {
    const backButton = document.getElementById('backButton');
    
    if (backButton) {
        backButton.addEventListener('click', () => {
            console.log('Back button clicked!');
        });
    } else {
        console.error('Could not find element with ID "backButton".');
    }
});

let currentPath = '/'

//DOM Elem
const folderContainer = document.getElementById('folderContainer');
const infoContainer = document.getElementById('infoContainer');
const backButton = document.getElementById('backButton');

async function fetchFolderContents(path, pushToHistory=true) {
    console.log(`Fetching contents for path: ${path}`); // Log the path being fetched
    try{
        const response = await fetch(`/api/directory?path=${encodeURI(path)}`);
        const data = await response.json();

        currentPath = path;//set the current path to the new path

        if (pushToHistory){
            const newurl = currentPath === '/' ? '/' : `/?path=${encodeURI(currentPath)}`;
            window.history.pushState({savedPath: currentPath}, "", newurl);
        }
        
        //render the UI with the new contents and stats
        console.log('Fetched data:', data); // Log the fetched data for debugging
        renderContents(data.contents);
        updateStats(data.stats)

        backButton.disabled = currentPath === '/';//disable back button if at root
    }
    catch (error) {
        console.error('Error fetching folder contents:', error);
    }

}

function renderContents(contents) {
    folderContainer.innerHTML = ''; // Clear previous contents

    folderContainer.style.placeItems = "start";
    folderContainer.style.gridTemplateColumns = "repeat(auto-fill, minmax(100px, 1fr))";
    folderContainer.style.gap = "20px";
    folderContainer.style.padding = "20px"; 


    contents.forEach(item => {
        const folderContainer = document.getElementById('folderContainer');
        folderContainer.style.display = 'grid';
        folderContainer.style.justifyContent = 'center';
        folderContainer.style.alignItems = 'center';

        const button = document.createElement('button');
        button.style.backgroundColor = 'transparent';
        button.style.border = 'none';
        button.style.cursor = 'pointer';
        button.style.display = 'flex';
        button.style.flexDirection = 'column';
        button.style.alignItems = 'center';
        button.style.gap = '10px';
        button.style.width = '120px';

        const icon = item.type === 'folder'? 'foldericon.png' : 'fileicon.png'; // Replace with actual icon paths
        button.innerHTML =
        `
        <img src="${icon}" alt="${item.name}" style="width: 50px; height: auto;">
        <p 
            style="margin: 0; text-align: center; width: 100%; 
            overflow: hidden; text-overflow: ellipsis; white-space: nowrap; 
            font-weight: bold; color: black;" title="${item.name}">
            ${item.name}
        </p>
        `;

        //Navigate to folder or download file on click
        button.addEventListener('click', () => {
            const newPath = item.path ? item.path : (currentPath === '/' ? `/${item.name}` : `${currentPath}/${item.name}`);
            if(item.type === 'folder'){
                fetchFolderContents(newPath);
            }
            else{
                window.location.href = `/api/file?path=${encodeURI(newPath)}`;
            }
        });
        folderContainer.appendChild(button);
    });
}


function updateStats(stats){
    infoContainer.innerHTML = `
        <p>Total Folders: ${stats.totalFolders > 0 ? stats.totalFolders : 0}</p>
        <p>Total Files: ${stats.totalFiles > 0 ? stats.totalFiles : 0}</p>
        <p>Total Size: ${stats.totalSize}</p>
    `;
}

backButton.addEventListener('click', () => {
    if (currentPath === '/') return; // Already at root, do nothing

    const pathSplit = currentPath.split('/');
    pathSplit.pop();
    const newPath = pathSplit.join('/') || '/'; // Ensure we don't end up with an empty string
    
    fetchFolderContents(newPath);
});

// Fetch the initial folder contents when the page loads
document.addEventListener('DOMContentLoaded', () => {
    const urlParam = new URLSearchParams(window.location.search);
    const startPath = urlParam.get('path') || '/'
    fetchFolderContents(startPath, false);
});

window.addEventListener('popstate', (event) => {
    const path = event.state ? event.state.savedPath : '/';
    fetchFolderContents(path, false);
})