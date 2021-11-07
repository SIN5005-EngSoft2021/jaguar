const table = document.getElementById("dataTable");
const rows = table.getElementsByTagName('tr');
const cells = (index) => rows[index].getElementsByTagName('td');
const itensSelect = document.getElementById("itensPerTable");
const range = document.getElementById("susRange");
const label = document.getElementById("rangeText");

startPagination(rows);


/**************************************************************
 * Add suspiciousness filter
***************************************************************/


function filterSuspiciousness() {
  const filter = parseFloat(range.value);
  rangeText.innerHTML = filter;


  for (let j=1; j<rows.length; j++){
    const tds = cells(j);
    const sc = document.getElementById("suspColumn");
    const item = tds.item(Array.from(sc.parentNode.children).indexOf(sc));

    if (parseFloat(item.textContent) > filter){
      rows[j].style.display = 'none';
    } else { rows[j].style.display = ''; }
  }

  const displayedRows = Array.from(rows).filter(el => el.style.display != 'none');
  startPagination(displayedRows);
}

range.addEventListener("change", filterSuspiciousness, false);


/**************************************************************
 * Add pagination
***************************************************************/
itensSelect.addEventListener("change", filterSuspiciousness, false);


function startPagination(displayedRows) {
  const filter = () => (itensSelect.value != 'all') ? parseInt(itensSelect.value) : displayedRows.length;
  createPagButtons(filter(), displayedRows);
  clickPagination(filter(), 1, displayedRows);
  rangeText.innerHTML = parseFloat(range.value);
}


function createPagButtons(filter, displayedRows){
  const s = displayedRows.length/filter;
  const r = displayedRows % filter;
  const pagination = document.getElementById("pagination");
  pagination.innerText = '';

  if (s === 1) return;
  else if (r > 0) s += 1;

  for (let i=0; i < s; i++) {
    const pag = document.createElement("button");
    pag.className = "pagButton";
    pag.name = i+1;
    pag.innerHTML = i+1;
    pag.disabled = false;
    pag.autocomplete = 'off';
    if ( i == 0) pag.disabled = true;

    pagination.appendChild(pag);
    pag.addEventListener("click", function(){
      const siblings = Array.from(pag.parentElement.children);
      siblings.forEach(s => s === pag? s.disabled = true : s.disabled = false);

      clickPagination(filter, pag.name, displayedRows);
    });
  }
}


function clickPagination(itensPerPage, page, displayedRows){
  const start = () => page == 1? 1: itensPerPage*(page-1) + 1;
  const end = start() + itensPerPage;

  for (let i = 1; i < displayedRows.length; i++){
    if (i>= start() && i < end)
    displayedRows[i].style.display = '';
    else displayedRows[i].style.display = 'none';
  }
}



