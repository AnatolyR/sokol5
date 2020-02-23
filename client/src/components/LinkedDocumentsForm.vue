<template>
    <div>
        <div v-if="errorMessage" class="s-dictionary-error-message">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-dictionary-loading s-dictionary-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="s-dictionary-container">
            <s-table :buttons="buttons" :loadData="loadData"
                     :columns="tableColumns"
                     :delete-url="`delete/links`"
                     :add-url="`${url}`"
                     :add-type="`${addType}`"
                     :object-id="objectId"
                     :object-type="objectType"
            ></s-table>
        </div>
    </div>
</template>

<style>

</style>

<script>
    import STable from '../components/Table';
    import axios from 'axios';

    export default {
        name: 's-linked-documents-form',
        components: {STable},
        props: {
            objectId: {},
            objectType: {}
        },
        mounted() {
            this.loading = false;
            this.loadActions();
        },
        methods: {
            loadActions() {
                axios.get(`/api/links/availableActions?objectId=${this.objectId}&objectType=${this.objectType}`)
                    .then((res) => {
                        res.data.forEach((a) => this.buttons[a] = true);
                    });
            },
            loadData(spec) {
                let url = `/api/${this.url}?objectId=${this.objectId}&objectType=${this.objectType}&size=${spec.size}&page=${spec.page}`;
                let sortProperty = spec.sortProperty === 'sizeStr' ? 'size' : spec.sortProperty;
                if (sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${sortProperty}`;

                    url += `&sort=${sortProperty},${spec.sortDirection}`;
                }
                if (spec.sortDirection) {
                    url += `&sortDirection=${spec.sortDirection}`;
                }

                let res = new Promise((resolve, reject) => {
                    axios.get(url, {
                        params: {
                            conditions: spec.conditions ? JSON.stringify(spec.conditions).slice(1, -1) : null
                        }
                    })
                        .then((res) => {
                            this.$emit("updateLinks");
                            res.data.content.forEach(link => {
                                link.title = "Связь с документом " + link.document.title;
                                link.id = link.link.id;
                            });
                            resolve(res);
                        })
                        .catch((error) => {
                            this.loading = false;
                            console.log(error);
                            this.errorMessage = 'Не удается загрузить данные';
                        });
                });
                return res;
            }
        },
        data() {
            return {
                loading: true,
                items: null,
                errorMessage: null,
                url: 'links/search/linksByObjectId',
                addType: 'link',
                buttons: {},
                tableColumns: [
                    {
                        "id": "select",
                        "title": "Выделение",
                        "type": "checkbox",
                        "ref": "link.id",
                        "visible": true
                    },
                    {
                        id: "link.type",
                        title: "Тип связи",
                        visible: true
                    },
                    {
                        id: "document.type",
                        title: "Тип документа",
                        visible: true
                    },
                    {
                        id: "document.title",
                        title: "Название",
                        visible: true
                    }
                ]
            }
        }
    }
</script>