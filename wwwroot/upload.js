document.addEventListener('DOMContentLoaded', () => {
    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', async (event) => {

            event.preventDefault(); // Prevent the default form submission
            uploadFiles(); // Call the upload function
        });
    } else {
        console.error('Could not find element with ID "uploadForm".');
    }
});

async function uploadFiles() {
    const fileInput = document.getElementById('file-upload');
    const files = fileInput.files;

    if (files.length === 0) {
        alert('Please select at least one file to upload.');
        return;
    }

    const formData = new FormData();
    for (const file of files) {
        formData.append('uploaded_file', file);
    }

    formData.append('path', currentPath); // Append the current path to the form data

    try {
        const response = await fetch('/upload', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            alert('Files uploaded successfully!');
            fileInput.value = ''; // Clear the file input after successful upload
            //refresh the folder contents here
            fetchFolderContents(currentPath);
        } else {
            const errorText = await response.text();
            alert(`Failed to upload files: ${errorText}`);
        }
    } catch (error) {
        console.error('Error uploading files:', error);
        alert('An error occurred while uploading files.');
    }
}