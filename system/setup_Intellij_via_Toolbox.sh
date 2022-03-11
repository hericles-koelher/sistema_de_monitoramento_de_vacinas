# Pequeno script para carregar a variavel de ambiente
# e abrir a IDE Intellij no mesmo terminal em que a variavel
# foi carregada. A chamada da

source ../sendgrid.env

# shellcheck disable=SC2046
eval $(find "$HOME/.local/share/JetBrains/Toolbox/apps/IDEA-U/" -name idea.sh)