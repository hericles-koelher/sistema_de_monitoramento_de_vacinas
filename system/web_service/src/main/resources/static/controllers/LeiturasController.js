async function getLeituras(){
    $('#main').load('leituras.html', () => {
        console.log('teste');
        $.get('http://localhost:8080/api/v1/leituras')
        .done(data => {
            console.log(data);
            preencheLeituras(data);
        });
    });
}

function getLeituraById(id){
    $('#main').load('leituras.html', () => {
        $.get(`http://localhost:8080/api/v1/leituras/${id}`)
        .done(data => {
            console.log(data);
            preencheLeituras(data);
        });
    });
}

function getLeiturasByCamara(id){
    $('#main').load('leituras.html', () => {
        $.get(`http://localhost:8080/api/v1/leituras/camara/${id}`)
        .done(data => {
            console.log(data);
            preencheLeituras(data);
        });
    });
}

function preencheLeituras(dados = ''){
    if(!Array.isArray(dados)){
        dados = [dados];
    } 

    dados.forEach(element => {
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
    else if(val == 1)
        getLeituraById(id);
    else getLeiturasByCamara(id);
}