function preencheLeituras(dados = ''){
    resultados = [
        {
            "id": 544,
            "data": "2022-03-15T13:15:13",
            "temperatura": 16.9,
            "coordenadaX": 16.9,
            "coordenadaY": 45.7,
            "idCamara": 1
        },
        {
            "id": 545,
            "data": "2022-03-15T13:32:18",
            "temperatura": 21.9,
            "coordenadaX": 23.5,
            "coordenadaY": 66.7,
            "idCamara": 2
        }
    ];

    //resultados = JSON.parse(resultados);

    resultados.forEach(element => {
        let table = document.getElementById('table_leituras');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let idCamara = document.createElement('td');
        idCamara.innerHTML = element.idCamara;
        tr.appendChild(idCamara);

        let data = document.createElement('td');
        let dataF = element.data.split('T')[0];
        dataF = (new Date(dataF)).toLocaleDateString('pt-BR', {timeZone: 'UTC'});
        data.innerHTML = dataF;                
        tr.appendChild(data);

        let hora = document.createElement('td');
        hora.innerHTML = element.data.split('T')[1];
        tr.appendChild(hora);

        let coordX = document.createElement('td');
        coordX.innerHTML = element.coordenadaX;
        tr.appendChild(coordX);

        let coordY = document.createElement('td');
        coordY.innerHTML = element.coordenadaY;
        tr.appendChild(coordY);

        table.appendChild(tr);
    });
}

function selectLeituras(sel){
    let input = document.getElementById('input_id');
    let btn = document.getElementById('filter');
    if(sel.value == 0)
        input.disabled = btn.disabled = 'disabled';                                         
    else input.disabled = btn.disabled = '';
}

function filtrar_leituras(){
    const val = document.getElementById('sel_filter').value;
    const id = document.getElementById('input_id').value;
    if(!id)
        alert("Forneça um ID válido!");
    //req
    //.then() : 
    //preencheLeituras(); //passa os dados
}