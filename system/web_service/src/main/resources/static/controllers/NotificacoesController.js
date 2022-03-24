async function getNotificacoes(){
    $('#main').load('notificacoes.html', () => {
        console.log('teste');
        $.get('http://localhost:8080/api/v1/notificacoes')
        .done(data => {
            console.log(data);
            preencheNotificacoes(data);
        });
    });
}

function getNotificacaoById(id){
    $('#main').load('notificacoes.html', () => {
        $.get(`http://localhost:8080/api/v1/notificacoes/${id}`)
        .done(data => {
            console.log(data);
            preencheNotificacoes(data);
        });
    });
}

function getNotificacoesByCamara(id){
    $('#main').load('notificacoes.html', () => {
        $.get(`http://localhost:8080/api/v1/notificacoes/camara/${id}`)
        .done(data => {
            console.log(data);
            preencheNotificacoes(data);
        });
    });
}

function atender_notificacao(id){
    $.ajax({
        url : `http://localhost:8080/api/v1/notificacoes/atender/${id}`,
        method : 'PUT'
    })
    .done(() => {
        document.getElementById('atender_' + id).innerHTML = '';
        document.getElementById('atendida_' + id).innerHTML = 'Sim';

    });
}

function preencheNotificacoes(dados = ''){
    /*dados = {
        id: 1,
        idCamara: 1,
        atendida: false
    };*/

    if(!Array.isArray(dados)){
        dados = [dados];
    } 

    dados.forEach(element => {
        let table = document.getElementById('table_notificacoes');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let idCamara = document.createElement('td');
        idCamara.innerHTML = element.idCamara;
        tr.appendChild(idCamara);

        let atendida = document.createElement('td');
        atendida.id = 'atendida_' + element.id;     

        let atender = document.createElement('td');
        atender.id = 'atender_' + element.id;
        let btn = document.createElement('button');
        btn.classList.add('btn_check');
        btn.innerHTML = '<i class="bi bi-check-circle-fill"></i>';
        btn.onclick = function (){
            atender_notificacao(element.id);
        }
        if(element.atendida == false){
            atender.appendChild(btn);
            atendida.innerHTML = 'Não';
        }    
        else atendida.innerHTML = 'Sim';

        tr.appendChild(atendida);
        tr.appendChild(atender);

        table.appendChild(tr);
    });
}


function selectNotificacoes(sel){
    let input = document.getElementById('input_id');
    let btn = document.getElementById('filter');
    if(sel.value == 0)
        input.disabled = btn.disabled = 'disabled';                                         
    else input.disabled = btn.disabled = '';
}

function filtrar_notificacoes(){
    const val = document.getElementById('sel_filter').value;
    const id = document.getElementById('input_id').value;
    if(!id)
        alert("Forneça um ID válido!");
    else if(val == 1)
        getNotificacaoById(id);
    else getNotificacoesByCamara(id);
}