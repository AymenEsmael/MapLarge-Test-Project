document.addEventListener('DOMContentLoaded', () => {
    const search = document.getElementById('searchForm');

    if (search){
        search.addEventListener('submit', (event) => {
            event.preventDefault();
            searchFiles()
        });
    }
    else{
        console.error('Could not find element with ID "searchForm".');
    }
});

async function searchFiles(){
    const searchInput = document.getElementById('search');

    // Validate the search input
    console.log(`Search input value: ${searchInput.value}`); // Log the search input value for debugging
    if (searchInput.value === null || searchInput.value.length === 0) {
        alert('Please enter a search query.');
        return;
    }
    const queryData = searchInput.value.trim();
    console.log(`Searching for: ${queryData}`); // Log the search query for debugging

    try{
        const response = await fetch(`/search?query=${encodeURIComponent(queryData)}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log(data)

        // data.content.map(d => fetchFolderContents(d.path));
        renderContents(data.contents);
        updateStats(data.stats);
        // document.getElementById('folderName').innerText = `Search Results for "${searchInput.value}"`;
        
    }
    catch (error){
        console.error('Error searching for files:', error);
    }

}