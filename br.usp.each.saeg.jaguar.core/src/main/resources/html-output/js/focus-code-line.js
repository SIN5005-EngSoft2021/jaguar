const fileName = document.URL;
const startLine = fileName.indexOf("#");

if (startLine !== -1) {
    focusCodeLine(fileName.substring(startLine + 1, fileName.length));
}

function focusCodeLine(id) {
    console.log(id);

    // locate element
    const line = document.getElementById(id);
    if (line === null) return;

    const parent = line.parent;
    const sign = document.createElement("div");
    sign.id = "focusedLine";



    // paint line
}



