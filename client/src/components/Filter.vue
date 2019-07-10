<template>
    <div class="card s-filter-buttons-bar" style="margin-bottom: 0.5em;">
        <div class="card-body">
            <h5 class="card-title">Фильтр</h5>
            <div>
                <div ref="filterRows" v-for="(row, index) in rows" style="margin-bottom: 0.5em;" :key="row.key">
                    <b-button size="sm" variant="danger" @click="removeRow(row.key)"><font-awesome-icon icon="trash" /></b-button>
                    <b-button size="sm" variant="success" @click="addRow(row.key)"><font-awesome-icon icon="plus" /></b-button>

                    <s-select class="" style="width: 100px; height: 32px; display: inline-block;
                        vertical-align: bottom; margin-right: 0.5em;"
                              :key="'conditionsSelect'"
                              :config="conditionsSelect" @value="(val) => row.condition=val"
                              :value="row.condition"
                              ></s-select>

                    <s-select v-if="row.condition === ''" class=""
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :key="'propertySelect'"
                              :config="propertySelect" @value="(val) => selectProperty(val, row)"
                              :value="row.property"
                    ></s-select>

                    <s-select v-if="row.condition === ''" class="" style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :key="'operationSelect'"
                              :config="operationSelect" @value="(val) => row.operation=val"
                              :value="row.operation"
                    ></s-select>

                    <b-form-input v-if="row.condition === '' && (!row.type || row.type === 'text' || row.type === 'link')" v-model="row.value" size="sm"
                                  style="display: inline-block; width: 200px; padding-top: 1px;"></b-form-input>

                    <date-picker v-if="row.condition === '' && row.type === 'date'"
                                 style="display: inline-block; width: 200px; padding-top: 1px; height: 31px;"
                            v-model="row.value"
                            :config="dateConfig"/>

                    <s-select v-if="row.condition === '' && row.type === 'documentType'" class=""
                              :key="'documentType'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="documentTypeConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>

                    <s-select v-if="row.condition === '' && row.type === 'documentKind'" class=""
                              :key="'documentKind'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="documentKindConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>

                    <s-select v-if="row.condition === '' && row.type === 'documentStatus'" class=""
                              :key="'documentStatus'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="documentStatusConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>

                    <s-select v-if="row.condition === '' && row.type === 'user'" class=""
                              :key="'userSelect'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="userSelectConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>

                    <s-select v-if="row.condition === '' && row.type === 'externalOrganization'" class=""
                              :key="'externalOrganization'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="externalOrganizationConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>

                    <s-select v-if="row.condition === '' && row.type === 'deliveryMethod'" class=""
                              :key="'deliveryMethod'"
                              style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="deliveryMethodConfig" @value="(val) => row.value=val"
                              :value="row.value"
                    ></s-select>
                </div>
            </div>
            <div>
                <b-button size="sm" :disabled="!isCleanActive" variant="danger" @click="cleanConditions()">Очистить</b-button>
                <b-button size="sm" variant="success" @click="addCondition()">Добавить условие</b-button>
                <b-button size="sm" variant="info" @click="update">Применить</b-button>
            </div>
        </div>
    </div>
</template>

<script>
    import SSelect from "../components/fields/Select";
    import datePicker from 'vue-bootstrap-datetimepicker';
    import axios from 'axios';

    export default {
        components: {SSelect, datePicker},
        name: 's-filter',
        mounted() {
            this.addCondition();
        },
        data() {
            return {
                rows: [],
                conditionsSelect: {
                    maxItems: 1,
                    valueField: 'value',
                    labelField: 'title',
                    preload: true,
                    options: [{title: ' ', value: ''},
                        {title: 'И (', value: 'and_block'},
                        {title: 'ИЛИ (', value: 'or_block'},
                        {title: ')', value: 'end_block'}],
                },
                operationSelect: {
                    maxItems: 1,
                    valueField: 'value',
                    labelField: 'title',
                    preload: true,
                    options: [
                        {title: ' ', value: ''},
                        {title: 'равно', value: 'EQUAL'},
                        {title: 'не равно', value: 'NOT_EQUAL'},
                        {title: 'больше', value: 'GREAT'},
                        {title: 'меньше', value: 'LESS'},
                        {title: 'больше или равно', value: 'GREAT_OR_EQUAL'},
                        {title: 'меньше или равно', value: 'LESS_OR_EQUAL'},
                        {title: 'содержит', value: 'LIKE'},
                        {title: 'начинается с', value: 'STARTS'},
                        {title: 'заканчивается на', value: 'ENDS'},
                        ],
                },
                documentTypeConfig: {
                    maxItems: 1,
                    valueField: 'value',
                    labelField: 'title',
                    preload: true,
                    options: [
                        {title: ' ', value: ''},
                        {title: 'Входящий', value: 'incoming'},
                        {title: 'Исходящий', value: 'outgoing'},
                        {title: 'Внутренний', value: 'internal'}
                    ],
                },
                documentKindConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'title',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    // create: true,
                    load(query, callback) {
                        axios.get(`/api/documentKinds?size=100`).then((res) => {
                            this.settings.load = null;
                            const users = res.data._embedded.documentKinds;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                documentStatusConfig: {
                    maxItems: 1,
                    valueField: 'value',
                    labelField: 'value',
                    preload: true,
                    options: [
                        {value: ' '},
                        {value: 'Черновик'},
                        {value: 'На рассмотрении'},
                        {value: 'На регистрации'},
                        {value: 'На исполнении'},
                        {value: 'На хранеии'}
                    ],
                },
                userSelectConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/users/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.users;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                externalOrganizationConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'id',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    load(query, callback) {
                        if (!query.length) {
                            return callback();
                        }
                        axios.get(`/api/contragents/search/userByTitle?title=%25${query}%25`).then((res) => {
                            const users = res.data._embedded.contragents;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                deliveryMethodConfig: {
                    maxItems: 1,
                    //plugins: ['remove_button'],
                    valueField: 'title',
                    labelField: 'title',
                    searchField: 'title',
                    preload: true,
                    // create: true,
                    load(query, callback) {
                        // if (!query.length) {
                        //     return callback();
                        // }
                        // axios.get(`/api/deliveryMethods/search/methodByTitle?title=%25${query}%25`).then((res) => {
                        axios.get(`/api/deliveryMethods?size=100`).then((res) => {
                            this.settings.load = null;
                            const users = res.data._embedded.deliveryMethods;
                            callback(users);
                        }).catch(() => {
                            callback();
                        })
                    }
                },
                dateConfig: {
                    locale:'ru'
                }
            }
        },
        computed: {
            isCleanActive() {
                return this.rows.length !== 0;
            },
        },
        methods: {
            selectProperty(val, row) {
                row.column=val;
                row.type = this.propertySelect.options.find((f) => f.id === val).type;
                row.value = '';
            },
            addCondition() {
                this.rows.push({key: this.uuidv4(), condition: '', type: ''});
            },
            addRow(key) {
                let index = this.rows.findIndex((e) => e.key === key);
                if (index >= 0) {
                    this.rows.splice(index, 0, {key: this.uuidv4(), condition: '', type: ''});
                }
            },
            removeRow(key) {
                let index = this.rows.findIndex((e) => e.key === key);
                if (index >= 0) {
                    this.rows.splice(index, 1);
                }
            },
            cleanConditions() {
                this.rows = [];
            },
            uuidv4() {
                return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                    return v.toString(16);
                });
            },
            update() {
                this.$emit('value', this.rows);
                this.$emit("update");
            }
        },
        props: {
            propertySelect: {},
            value: {}
        }
    }
</script>

<style>
    .s-filter-buttons-bar button {
        margin-right: 0.5em;
    }

    .s-filter-buttons-bar .selectize-input {
        min-height: 0 !important;
        height: 31px;
        overflow: unset !important;
        line-height: 1.15 !important;
        margin-top: 1px;
    }

    .s-filter-buttons-bar .selectize-dropdown-content > .option {
        min-height: 24px;
    }
    
</style>