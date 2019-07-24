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
                     :delete-url="`delete/role`"
                     :add-url="`role`"
                     :add-type="`${addType}`"
                     :object-id="userId"
                     :object-type="'role'"
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
        name: 's-roles-form',
        components: {STable},
        props: {
            userId: {},
            buttons: {}
        },
        mounted() {
            this.loading = false;
            this.loadActions();
        },
        methods: {
            loadActions() {
                // axios.get(`/api/attaches/availableActions?objectId=${this.objectId}&objectType=${this.objectType}`)
                //     .then((res) => {
                //         res.data.forEach((a) => this.buttons[a] = true);
                //     });
            },
            loadData(spec) {
                let url = `/api/${this.url}?userId=${this.userId}&size=${spec.size}&page=${spec.page}`;
                if (spec.sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${spec.sortProperty}`;

                    url += `&sort=${spec.sortProperty},${spec.sortDirection}`;
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
                url: 'user/search/roles',
                addType: 'role',
                tableColumns: [
                    {
                        "id": "select",
                        "title": "Выделение",
                        "type": "checkbox",
                        "ref": "id",
                        "visible": true
                    },
                    {
                        id: "id",
                        title: "ИД",
                        visible: false
                    },
                    {
                        id: "title",
                        title: "Роль",
                        visible: true
                    },
                    {
                        id: "groupName",
                        title: "Группа",
                        visible: true
                    }
                ]
            }
        }
    }
</script>