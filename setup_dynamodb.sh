#!/bin/bash

# Configurações
ENDPOINT_URL=http://localhost:4566
REGION=us-east-1

# Função para verificar se um comando existe
command_exists () {
    type "$1" &> /dev/null ;
}

# Verifica se o AWS CLI está instalado
if ! command_exists aws; then
    echo "Erro: AWS CLI não encontrado. Por favor, instale-o para continuar."
    exit 1
fi

echo "Configurando tabelas do DynamoDB via LocalStack..."

# 1. Criar a tabela celulares_tbl
echo "Criando tabela: celulares_tbl"
aws dynamodb create-table \
    --endpoint-url=$ENDPOINT_URL \
    --region $REGION \
    --table-name celulares_tbl \
    --attribute-definitions AttributeName=id_celular,AttributeType=S \
    --key-schema AttributeName=id_celular,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 > /dev/null

if [ $? -eq 0 ]; then
    echo "Tabela celulares_tbl criada com sucesso."
else
    echo "Aviso: Falha ao criar a tabela celulares_tbl. Ela pode já existir."
fi

# 2. Criar a tabela transacao_tbl
echo "Criando tabela: transacao_tbl"
aws dynamodb create-table \
    --endpoint-url=$ENDPOINT_URL \
    --region $REGION \
    --table-name transacao_tbl \
    --attribute-definitions \
        AttributeName=id_transacao,AttributeType=S \
        AttributeName=id_celular,AttributeType=S \
    --key-schema \
        AttributeName=id_transacao,KeyType=HASH \
        AttributeName=id_celular,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 > /dev/null

if [ $? -eq 0 ]; then
    echo "Tabela transacao_tbl criada com sucesso."
else
    echo "Aviso: Falha ao criar a tabela transacao_tbl. Ela pode já existir."
fi

# 3. Inserir dados iniciais na celulares_tbl
echo "Inserindo item inicial na celulares_tbl..."
aws dynamodb put-item \
    --endpoint-url=$ENDPOINT_URL \
    --region $REGION \
    --table-name celulares_tbl \
    --item '{
        "id_celular": {"S": "123"},
        "preco": {"N": "9750"},
        "ultima_atualizacao": {"S": "2024-05-21T10:00:00Z"}
    }' > /dev/null

aws dynamodb put-item \
    --endpoint-url=$ENDPOINT_URL \
    --region $REGION \
    --table-name celulares_tbl \
    --item '{
        "id_celular": {"S": "456"},
        "preco": {"N": "8999"},
        "ultima_atualizacao": {"S": "2024-05-20T11:30:00Z"}
    }' > /dev/null

if [ $? -eq 0 ]; then
    echo "Itens inseridos com sucesso na celulares_tbl."
else
    echo "Aviso: Falha ao inserir itens. Eles podem já existir ou a tabela não foi criada corretamente."
fi

echo "
Configuração do DynamoDB concluída!"
