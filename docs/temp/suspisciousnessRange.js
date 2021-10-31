const table = document.getElementsByTagName('table')[0];
const rows = table.getElementsByTagName('tr');
const cells = (index) => rows[index].getElementsByTagName('td');
addMeterColumns();


/**************************************************************
 * Add suspiciousness filter
***************************************************************/
const container = document.getElementsByClassName("executionConfigs")[0];

let paragraph = document.createElement("p");
let special = document.createElement("em");
let text = document.createTextNode("Suspiciousness Filter: ");
let range = document.createElement("input");
range.type = "range";
range.id = "susRange";
range.min = "0";
range.max = "1";
range.step = "0.5";

paragraph.append(special.appendChild(text),range);
container.appendChild(paragraph);


range.addEventListener("change", function() {
    const filter = parseFloat(range.value);
   

    for (let j=1; j<rows.length; j++){
      const tds = cells(j);
      const item = tds.item(tds.length-2);

      if (parseFloat(item.textContent) > filter){
        rows[j].style.display = 'none';
      } else {
        rows[j].style.display = '';
      }
    }

}, false);



/**************************************************************
 * Add table header tooltip
***************************************************************/
const headerItems = rows[0].getElementsByTagName('th');
const interest = ['CEF', 'CEP', 'CNP', 'CNF'];
const title = ['the number of failed test cases that executed the component',
    'the number of passed test cases that executed the component',
    'the number of passed test cases that did not execute the component',
    'the number of failed test cases that did not execute the component']

addTooltip();

function addTooltip () {
  for(let i = 2; i < headerItems.length; i++){
    const el = headerItems[i];
    const index = interest.indexOf(el.innerText);
    if (index > -1){
      console.log("achei "+index);
      el.innerHTML = 
      `<abbr title='${title[index]}'>${el.innerText}</abbr>`;
    }
  }
}

// descrição retirada pag 31: https://www.teses.usp.br/teses/disponiveis/100/100131/tde-18102016-092654/publico/dissertacao.pdf



/**************************************************************
 * Add meter bar column
***************************************************************/


function addMeterColumns() {
  let newColumn = document.createElement("th");
  newColumn.innerText = "Bar";
  rows[0].append(newColumn);
  for (let j=1; j<rows.length; j++){
    const tds = cells(j);
    const item = tds.item(tds.length-1);
  
    
    const sus = parseFloat(item.textContent);
    console.log(sus);
    let newCell = document.createElement("td");
    const meter = document.createElement("meter");
    meter.min = 0;
    meter.max = 1;
    meter.value = sus;
    meter.innerText = sus;
    meter.optimum = 0;
    newCell.append(meter);//
    rows[j].append(newCell);
  }//
}
