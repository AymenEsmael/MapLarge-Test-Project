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