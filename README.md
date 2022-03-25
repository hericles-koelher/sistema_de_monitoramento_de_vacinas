## Informações

Este trabalho está sendo desenvolvido durante o curso
de "Sistemas Baseados em Eventos".

### Modulos planejados:

* Simulador de Dados (Sensores)
* Sistema

### Requisitos:
* Java 11
* Kafka
* MySQL
* Postman (Opcional)

### Execução:
Para executar o projeto, antes garanta que o Kafka e o MySQL estão em execução 
e ajuste este [arquivo](./system/data/src/main/resources/data.properties)
para que a aplicação possa ter acesso ao banco e criar a base de dados. Feito
isto, basta executar todos os módulos do [sistema](./system) e abrir o endereço
localhost:8080 para poder interagir com o sistema.

OBS: Infelizmente não adicionamos na interface a possibilidade de 
cadastrar o gestor responsavel por uma câmara, assim como também não 
fizemos a interface de cadastro do gestor, por isso para realizar 
essas ações é necessário utilizar uma chamada direta para API.

#### Exemplo de Requisições
Para realizar requisições diretas para API, utilize os exemplos feitos no Postman.
Os arquivos para importar os exemplos se encontram na pasta [requisicoes](./requisicoes).
