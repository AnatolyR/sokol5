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
        name: 's-history-form',
        components: {STable},
        props: {
            objectId: {},
            objectType: {}
        },
        mounted() {
            this.loading = false;
            // this.loadActions();
        },
        methods: {
            // loadActions() {
            //     axios.get(`/api/attaches/availableActions?objectId=${this.objectId}&objectType=${this.objectType}`)
            //         .then((res) => {
            //             res.data.forEach((a) => this.buttons[a] = true);
            //         });
            // },
            loadData(spec) {
                let url = `/api/document/${this.objectId}/history?size=${spec.size}&page=${spec.page}`;
                let sortProperty = spec.sortProperty === 'sizeStr' ? 'size' : spec.sortProperty;
                if (sortProperty && spec.sortDirection) {
                    url += `&sortProperty=${sortProperty}`;

                    url += `&sort=${sortProperty},${spec.sortDirection.toLowerCase()}`;
                } else if (spec.sortDirection) {
                    url += `&sortDirection=${spec.sortDirection}`;
                } else {
                    url += `&sort=createDate,desc`;
                }

                let res = new Promise((resolve, reject) => {
                    axios.get(url, {
                        params: {
                            conditions: spec.conditions ? JSON.stringify(spec.conditions).slice(1, -1) : null
                        }
                    })
                        .then((res) => {
                            // this.$emit("updateAttaches");
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
                buttons: {
                    refresh: false,
                    columns: false
                },
                tableColumns: [
                    {
                        id: "createDate",
                        title: "Дата изменения",
                        visible: true,
                        type: 'datetime'
                    },
                    {
                        id: "type",
                        title: "Тип",
                        visible: true
                    },
                    {
                        id: "userTitle",
                        title: "Пользователь",
                        visible: true
                    },
                    {
                        id: "data",
                        title: "Информация",
                        type: "detailsRow",
                        visible: true
                    }
                ]
            }
        }
    }
</script>