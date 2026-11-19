
// variables to be manipulated
const fileModal = document.getElementById('fileActionModal');
const modalFileName = document.getElementById('modalFileName');
let currentSelectedFilePath = '';
let currentSelectedFileName = '';
let currentSelectedType = '';

// render the web page based on the new contents fetched by the backend
function renderContents(contents) {
    const folderContainer = document.getElementById('folderContainer')
    folderContainer.innerHTML = ''; // Clear previous contents

    //Styling for the content
    folderContainer.style.placeItems = "start";
    folderContainer.style.gridTemplateColumns = "repeat(auto-fill, minmax(100px, 1fr))";
    folderContainer.style.gap = "20px";
    folderContainer.style.padding = "20px"; 
    folderContainer.style.display = 'grid';
    folderContainer.style.justifyContent = 'center';
    folderContainer.style.alignItems = 'center';

    //for each item fetched
    contents.forEach(item => {
        
        const button = document.createElement('button');
        button.type = 'button';
        button.style.backgroundColor = 'transparent';
        button.style.border = 'none';
        button.style.cursor = 'pointer';
        button.style.display = 'flex';
        button.style.flexDirection = 'column';
        button.style.alignItems = 'center';
        button.style.gap = '10px';
        button.style.width = '120px';

        //checks if the item is a folder or a file and gets the appropirte icon
        const icon = item.type === 'folder'? 'foldericon.png' : 'fileicon.png'; // Replace with actual icon paths

        //Dynamically names the file based on what was fetched
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
        
        const newPath = item.path ? item.path : (currentPath === '/' ? `/${item.name}` : `${currentPath}/${item.name}`);
        //Navigate to folder or download file on click
        button.addEventListener('click', () => {
            if(item.type === 'folder'){
                fetchFolderContents(newPath);
            }
            else if (item.type === 'file') {
                
                currentSelectedFilePath = newPath;
                currentSelectedFileName = item.name;
                currentSelectedType = 'file';
                modalFileName.textContent = item.name;

                // console.log(currentSelectedFileName, currentSelectedFilePath);
                
                fileModal.showModal(currentSelectedFilePath, currentSelectedFileName); 
            }
        });

        //Right click to delete a folder
        if (item.type === 'folder') {
            button.title = `${item.name} (Right-click to delete)`;
            button.addEventListener('contextmenu', (e) => {
                e.preventDefault();
                currentSelectedFilePath = newPath;
                currentSelectedFileName = item.name;
                currentSelectedType = 'folder';
                document.getElementById('btnDelete').click();
            });
        }
        folderContainer.appendChild(button);
    });
}

// updates the states on the current screen
function updateStats(stats){
    infoContainer.innerHTML = `
        <p>Total Folders: ${stats.totalFolders > 0 ? stats.totalFolders : 0}</p>
        <p>Total Files: ${stats.totalFiles > 0 ? stats.totalFiles : 0}</p>
        <p>Total Size: ${stats.totalSize}</p>
    `;
}


backButton.addEventListener('click', () => {
    if (new URLSearchParams(window.location.search).has('search')){
        fetchFolderContents(currentPath);
        return;
    }
    if (currentPath === '/') return; // Already at root, do nothing

    const pathSplit = currentPath.split('/');
    pathSplit.pop();
    const newPath = pathSplit.join('/') || '/'; // ensure we don't end up with an empty string
    
    fetchFolderContents(newPath);
});


//Deep linkable URL
window.addEventListener('popstate', (event) => {
    const urlParam = new URLSearchParams(window.location.search);
    const searchQuery = (event.state && event.state.savedSearch) || urlParam.get('search');
    const path = (event.state && event.state.savedPath) || urlParam.get('path') || '/';

    if (searchQuery) {
        searchFiles(searchQuery, false);
    } else {
        fetchFolderContents(path, false);
    }
})

//Home button takes us to the Starting/Home directory
const homeButton = document.getElementById('homeButton').addEventListener('click', () => {
    fetchFolderContents('/');
});


//Downloads the file
document.getElementById('btnDownload').addEventListener('click', () => {
    window.location.href = `/api/file?path=${encodeURIComponent(currentSelectedFilePath)}`;
    fileModal.close();//closes the pop up window after clicking the file
});

//Opens the file
document.getElementById('btnOpen').addEventListener('click', async () => {
    console.log("HIIII: ${currentSelectedFileName}, ${newPath}")
    window.open(`/api/open?path=${encodeURIComponent(currentSelectedFilePath)}`, '_blank');
    fileModal.close();
});


//Deletes file and folder
document.getElementById('btnDelete').addEventListener('click', async () => {
    //confirms before deleting
    const confirmDelete = confirm("Are you sure you wanted to delete " + currentSelectedFileName);
    if (confirmDelete){
        try{
            // console.log("going to Delete Controller");
            //calls the c# delete end point
            const response = await fetch(`/api/delete?path=${encodeURIComponent(currentSelectedFilePath)}&type=${encodeURIComponent(currentSelectedType)}`, {
                method:'Delete'
            });
            const data = await response.json();
            console.log(data);
            
            if (data.message === "Success"){
                alert(currentSelectedFileName + " has been successfuly deleted");
                fileModal.close();
                fetchFolderContents(currentPath);
            }
        }catch(err){
            // alert("Try again");
            console.error(err.message + " something went wrong trying to delete the file");
            
            }
        }
});

//closing the menu
document.getElementById('btnCloseModal').addEventListener('click', () => {
    fileModal.close();
});