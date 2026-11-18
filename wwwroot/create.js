

const form = document.getElementById('createFrom');
form.addEventListener('submit', (event) =>{

    event.preventDefault();
    const clicked = event.submitter;
    
    if (clicked.id === 'createFolder'){
        const folderName = prompt(
            "Please enter your folder name: "
        );

        if (!folderName || folderName.trim() === "") return;
        create(folderName, "folder");
        
    }
    else if (clicked.id === 'createFile'){
        const fileName = prompt(
            "Please enter your file name and extension: "
        );

        if (!fileName || fileName.trim() === "") return;
        create(fileName, "file");
    }

});

async function create(name, type) {
    // console.log("YES");
    if (!name) return;

    const formData = new FormData();
    formData.append('path', currentPath === '/' ? `/${name}` : `${currentPath}/${name}`)
    formData.append('type', type);
    
    try{
        const response = await fetch('/create', {
            method:"POST",
            body:formData
        })
        if (response.ok){
            fetchFolderContents(currentPath);
        }
        else{
            alert("Error: " + await response.json())
        }
       
        
    }catch (error){
        console.error(error + (err.error || err.title || "Unknown error occurred"));
    }

}
    

