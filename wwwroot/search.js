//Gets the item to be search and searches it
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


async function searchFiles(queryOverride=null, pushToHistory=true){
    const searchInput = document.getElementById('search');
    const queryData = (queryOverride !== null ? queryOverride : searchInput.value).trim();

    //if search field is empty alert the user
    if (!queryData){
        alert("Please enter a search query");
        return;
    }
    //gets the value to be search
    searchInput.value = queryData;

    try{
        //calls the search c# end point
        const response = await fetch(`/api/search?query=${encodeURIComponent(queryData)}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        // console.log(data)

        //For deep link URL
        if (pushToHistory) {
            const newUrl = `/?search=${encodeURIComponent(queryData)}`;
            window.history.pushState(
                { savedSearch: queryData },
                "",
                newUrl
            );
        }

        renderContents(data.contents);
        updateStats(data.stats);
        backButton.disabled = false;

        //If nothing was found
        if (data.stats.totalFolders === 0 && data.stats.totalFiles === 0) {
            alert("No item was found");
        }

        
    }catch (error){
        console.error('Error searching for files:', error);
    }
}