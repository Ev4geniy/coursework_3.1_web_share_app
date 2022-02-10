let requestPath;
let modal;
let listing;
let uploader;
let dropArea;

function getPage(name){

    let params = 'dir=' + name.valueOf();//+encodeURIComponent(name);
    console.log(params);
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById("treePage").innerHTML = this.responseText;
            // console.log(this.responseText);
        }
    };
    xhr.open("POST", requestPath, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(params);
}

function setup(){
    requestPath = document.getElementById("requestPath").getAttribute("value");
    getPage('');

    modal = document.getElementById("myModal");
    if(modal !== null){
        let span = document.getElementById("share-closer");
        span.onclick = function() {
            dropShareModSelectors();
        }
        dropShareModSelectors();
    }
    uploader = document.getElementById("file-uploader");
    if(uploader !== null){
        document.getElementById("uploader-closer").onclick = function (){
            uploader.style.display = "none";
        }

        dropArea = document.getElementById("drop-area");
        ;['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, preventDefaults, false)
        });


        ;['dragenter', 'dragover'].forEach(eventName => {
            dropArea.addEventListener(eventName, highlightUploader, false)
        });

        ;['dragleave', 'drop'].forEach(eventName => {
            dropArea.addEventListener(eventName, unhighlightUploader, false)
        });

        dropArea.addEventListener('drop', handleDrop, false);
    }

    //alert(requestPath);
}

function deleteFile(currentPath){
    if(confirm("deleting:"+currentPath)){
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {

                currentPath = currentPath.substr(0,currentPath.lastIndexOf("/"));
                getPage(currentPath);
            }
        };
        xhr.open("POST", "./delete", true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send("name=" + currentPath.valueOf());
    }
}
function updateFile(e){

}

function share(filename){
    let user = document.getElementById("userToShare").value;
    let isWritable = document.getElementById("writable").checked;           //if (!name) name = "/";
    filename = document.getElementById("fileNameToShare").getAttribute("value");

    //name ="/f1"
    //let group = prompt("Please enter groupId:");

    let params = 'filename=' + filename.valueOf() + "&user=" + user.valueOf();      //+encodeURIComponent(name);

    if (isWritable){
        params += "&writable=";
    }
    console.log(params);
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {

            //console.log(this.responseText);
        }
    };
    xhr.open("POST", "share", true);
    //xhr.open("POST", "http://127.0.0.1:8001", true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(params);
}

function shareToAll(){
    let filename = document.getElementById("fileNameToShare").getAttribute("value");
    let params = "filename="+filename;
    console.log(params);
    let xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            let res = document.getElementById("random-link");
            let link = "http://localhost:8080/cursework_war/listpublic?id=" + xhr.responseText;
            res.setAttribute("href",link)
            res.innerHTML = "http://localhost:8080/cursework_war/listpublic?id="+xhr.responseText;
            //console.log(this.responseText);
        }
    };
    xhr.open("POST", "sharetoall", true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(params);

}

function createDirectory(currentPath){

    // let text;
    let dirName = prompt("Please enter folder name:");
    if (dirName != null && dirName !== "") {
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                //document.getElementById("treePage").innerHTML = xhr.responseText;
                getPage(currentPath);
            }
        };
        xhr.open("POST", "./create", true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.send("path=" + currentPath + "&dirname=" + dirName + "&creating=directory");
    }
}

function showShareMenu(filename){

    document.getElementById("fileNameToShare").setAttribute("value", filename);
    modal.style.display = "block";

}

function dropShareModSelectors(){
    modal.style.display = "none";
    let selectors = document.querySelector('input[name="share-mode"]:checked');
    if(selectors) selectors.checked = false;
    document.getElementById("share-with-user-div").style.display = "none";
    document.getElementById("share-by-link-div").style.display = "none";
    document.getElementById("userToShare").value = "";
    document.getElementById("writable").checked = false;
    document.getElementById("fileNameToShare").setAttribute("value", "");
    document.getElementById("random-link").innerHTML = "To generate link click on \"Share\"";
}


function showToUser(){
    document.getElementById("share-with-user-div").style.display = "block";
    document.getElementById("share-by-link-div").style.display = "none";
}

function showToLink(){
    document.getElementById("share-with-user-div").style.display = "none";
    document.getElementById("share-by-link-div").style.display = "block";
}

function downloadFile(filename, forPublic, shared){

    window.location.href = (
        forPublic ? "./downloadpublic" : "./download")
        + "?file="
        + filename
        + (shared || forPublic ? "&shared=" : "");
}

(function() {

    /**
     * Function to check if we clicked inside an element with a particular class
     * name.
     *
     * @param {Object} e The event
     * @param {String} className The class name to check against
     * @return {Boolean | EventTarget}
     */
    function clickInsideElement( e, className ) {
        let el = e.target;

        if ( el.classList.contains(className) ) {
            return el;
        }
        return false;
    }

    /**
     * Get's exact position of event.
     *
     * @param {Object} e The event passed in
     * @return {Object} Returns the x and y position
     */
    function getPosition(e) {
        let posX = 0;
        let posY = 0;

        if (!e)
            e = window.event;

        if (e.pageX || e.pageY) {
            posX = e.pageX;
            posY = e.pageY;
        } else if (e.clientX || e.clientY) {
            posX = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
            posY = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
        }

        return {
            x: posX,
            y: posY
        }
    }

    /**
     * Variables.
     */
    let contextMenuItemClassName = "context-menu__item";
    let activator = "context-menu-active";

    let fileItemInContext;
    let directoryItemInContext;
    let listingItemInContext;

    let clickCoords;
    let clickCoordsX;
    let clickCoordsY;

    let listingMenu     = document.querySelector("#listing-context");
    let fileMenu        = document.querySelector("#file-context");
    let directoryMenu   = document.querySelector("#directory-context");
    let menuState = 0;
    let menuWidth;
    let menuHeight;

    let windowWidth;
    let windowHeight;

    /**
     * Initialise our application's code.
     */
    function init() {
        contextListener();
        clickListener();
        keyupListener();
        resizeListener();
    }

    /**
     * Listens for contextmenu events.
     */
    function contextListener() {
        document.addEventListener( "contextmenu", function(e) {
            // taskItemInContext = clickInsideElement( e, taskItemClassName );

            fileItemInContext       = clickInsideElement(e, "file");
            directoryItemInContext  = clickInsideElement(e, "folder");
            listingItemInContext    = clickInsideElement(e, "file-listing");

            if(menuState !== 0){
                toggleMenuOff();
            }

            if(fileItemInContext && fileMenu !== null){
                e.preventDefault();
                turnFileMenuOn();
                positionMenu(e, fileMenu);
                directoryItemInContext = null;
                listingItemInContext = null;
            }
            else if(directoryItemInContext && directoryMenu !== null){
                e.preventDefault();
                turnDirectoryMenuOn();
                positionMenu(e, directoryMenu);
                fileItemInContext = null;
                listingItemInContext = null;
            }
            else if(listingItemInContext && listingMenu !== null){
                e.preventDefault();
                turnListingMenuOn();
                positionMenu(e, listingMenu);
                fileItemInContext = null;
                directoryItemInContext = null;
            }
            else {
                fileItemInContext = null;
                directoryItemInContext = null;
                listingItemInContext = null;
                toggleMenuOff();
            }
        });
    }

    /**
     * Listens for click events.
     */
    function clickListener() {
        document.addEventListener( "click", function(e) {
            let clickElIsLink = clickInsideElement( e, contextMenuItemClassName );

            if ( clickElIsLink ) {
                e.preventDefault();
                menuItemListener( clickElIsLink );
            } else {
                let button = e.button;
                if ( button === 0 ) {
                    toggleMenuOff();
                }
            }
        });
    }

    /**
     * Listens for keyup events.
     */
    // ToDo: look for beater alternative to keyKode
    function keyupListener() {
        window.onkeyup = function(e) {
            if ( e.keyCode === 27 ) {
                toggleMenuOff();
            }
        }
    }

    /**
     * Window resize event listener
     */
    function resizeListener() {
        window.onresize = function(e) {
            toggleMenuOff();
        };
    }

    /**
     * Turns the custom context menu on.
     */

    function turnFileMenuOn(){
        if(menuState !== 1){
            menuState = 1;
            turnDirectoryMenuOff();
            turnListingMenuOff();
            fileMenu.classList.add(activator);
        }
    }

    function turnDirectoryMenuOn(){
        if(menuState !== 1){
            menuState = 1;
            turnFileMenuOff();
            turnListingMenuOff();
            directoryMenu.classList.add(activator);
        }
    }

    function turnListingMenuOn(){
        if(menuState !== 1){
            menuState = 1;
            turnFileMenuOff();
            turnDirectoryMenuOff();
            listingMenu.classList.add(activator);
        }
    }

    /**
     * Turns the custom context menu off.
     */
    function toggleMenuOff() {
        if ( menuState !== 0 ) {
            menuState = 0;
            turnFileMenuOff();
            turnListingMenuOff();
            turnDirectoryMenuOff();
        }
    }

    function turnFileMenuOff(){
        if(fileMenu !== null) {
            fileMenu.classList.remove(activator);
        }
    }

    function turnDirectoryMenuOff(){
        if(directoryMenu !== null) {
            directoryMenu.classList.remove(activator);
        }
    }

    function turnListingMenuOff(){
        if(listingMenu !== null) {
            listingMenu.classList.remove(activator);
        }
    }

    /**
     * Positions the menu properly.
     *
     * @param {Object} e The event
     * @param {HTMLElement} menu The menu to set
     */
    function positionMenu(e, menu) {
        clickCoords = getPosition(e);
        clickCoordsX = clickCoords.x;
        clickCoordsY = clickCoords.y;

        menuWidth = menu.offsetWidth + 4;
        menuHeight = menu.offsetHeight + 4;

        windowWidth = window.innerWidth;
        windowHeight = window.innerHeight;

        if ( (windowWidth - clickCoordsX) < menuWidth ) {
            menu.style.left = windowWidth - menuWidth + "px";
        } else {
            menu.style.left = clickCoordsX + "px";
        }

        if ( (windowHeight - clickCoordsY) < menuHeight ) {
            menu.style.top = windowHeight - menuHeight + "px";
        } else {
            menu.style.top = clickCoordsY + "px";
        }
    }

    /**
     * Dummy action function that logs an action when a menu item link is clicked
     *
     * @param {HTMLElement | EventTarget} link The link that was clicked
     */
    function menuItemListener( link ) {

        let action = link.getAttribute("data-action");

        if(fileItemInContext){

            let filename = fileItemInContext.getAttribute("data-id");

            if(action === "Update_file"){
                console.log("not allowed yet");
            }
            else if(action === "Download_file"){
                downloadFile(filename, false, false);
            }
            else if(action === "Share_file"){
                showShareMenu(filename);
            }
            else if(action === "Delete_file"){
                deleteFile(filename);
            }
            else {
                console.log("wrong action: " + action);
            }
            console.log( "File path: " + fileItemInContext.getAttribute("data-id"));
        }
        else if(directoryItemInContext){

            let filename = directoryItemInContext.getAttribute("data-id");

            if(action === "Share_directory"){
                showShareMenu(filename);
            }
            else if(action === "Delete_directory"){
                deleteFile(filename);
            }
            else {
                console.log("wrong action: " + action);
            }
            console.log( "Directory path: " + directoryItemInContext.getAttribute("data-id"));
        }
        else if(listingItemInContext){

            let currentPath = listingItemInContext.getAttribute("data-id");
            if(action === "Create_directory"){
                createDirectory(currentPath);
            }
            else if(action === "Upload_file" || action === "Upload_file_to_shared"){
                if (uploader !== null){
                    uploader.style.display = "block";
                }
            }
            else if(action === "Share_current_directory"){
                showShareMenu(currentPath);
            }
            else {
                console.log("wrong action: " + action);
            }
            console.log( "Listing path: " + listingItemInContext.getAttribute("data-id"));
        }
        else {
            console.log("Undefined item")
        }
        console.log(", action - " + action);
        toggleMenuOff();
    }

    /**
     * Run the app.
     */
    init();

})();

function preventDefaults (e) {
    e.preventDefault();
    e.stopPropagation();
}

function highlightUploader(e) {
    dropArea.classList.add('highlight');

}function unhighlightUploader(e) {
    dropArea.classList.remove('highlight');
}

function handleDrop(e) {
    let dt = e.dataTransfer;
    let files = dt.files;
    handleFiles(files);
}

function handleFiles(files) {
    ([...files]).forEach(uploadFile);
}

function uploadFile(file) {
    let url = './create';
    let xhr = new XMLHttpRequest();
    let formData = new FormData();
    let requestPath = document.getElementById("requestPath").getAttribute("value");
    let currentDirectory = document.getElementById("currentDir").getAttribute("value");
    let isShared;

    if(requestPath === "listing"){
        isShared = false;
    }
    else if(requestPath === "listshared"){
        alert("IS_SHARED");
        isShared = true;
    }
    else if(requestPath === "listpublic"){
        console.log("Upload is not allow from public");
        return;
    }
    else {
        console.log("Unknown request path! Upload imposable");
        return;
    }

    if (isShared){
        formData.append("shared", "");
    }

    formData.append("creating", "file");
    formData.append("path", currentDirectory);


    xhr.open('POST', url, true);
    xhr.addEventListener('readystatechange', function(e) {
        if (xhr.readyState === 4 && xhr.status === 200) {
            alert("Uploaded!");
            getPage(currentDirectory);
        }
        else if (xhr.readyState === 4 && xhr.status === 418) {
            alert("U can't upload files to this directory!");
        }
        else if (xhr.readyState === 4){
            alert("Unknown response. Status = " + xhr.status);
        }
        uploader.style.display = "none";
    });
    formData.append('file', file, file.name)
    xhr.send(formData)
}
