

const form = document.getElementById('createFrom');
form.addEventListener('submit', (event) =>{

    event.preventDefault();
    const clicked = event.submitter;
    
    //When creating a folder/file the user is prompeted for the folder/file name
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

    //creates a FormData to send to the c# backend to create a folder/file
    const formData = new FormData();
    formData.append('path', currentPath === '/' ? `/${name}` : `${currentPath}/${name}`)
    formData.append('type', type);
    
    try{
        const response = await fetch('/api/create', {
            method:"POST",
            body:formData
        })
        //on success gets the content again to show the new item
        if (response.ok){
            fetchFolderContents(currentPath);
        }
        else{
            const errData = await response.json();
            alert("ERROR: " + (errData.error))
        }
       
        
    }catch (error){
        console.error("Create failed: ", error);
    }

}
    

