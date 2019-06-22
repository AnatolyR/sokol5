<template>
    <div class="card s-filter-buttons-bar" style="margin-bottom: 0.5em;">
        <div class="card-body">
            <h5 class="card-title">Фильтр</h5>
            <div>
                <div ref="filterRows" v-for="(row, index) in rows" style="margin-bottom: 0.5em;" :key="row.key">
                    <b-button size="sm" variant="danger" @click="removeRow(row.key)"><font-awesome-icon icon="trash" /></b-button>
                    <b-button size="sm" variant="success" @click="addRow(row.key)"><font-awesome-icon icon="plus" /></b-button>

                    <s-select class="" id="conditionsSelect" style="width: 100px; height: 32px; display: inline-block;
                        vertical-align: bottom; margin-right: 0.5em;"
                              :config="conditionsSelect" @value="(val) => row.condition=val"
                              :value="row.condition"
                              ></s-select>

                    <s-select v-if="row.condition === ''" class="" id="propertySelect" style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="propertySelect" @value="(val) => row.column=val"
                              :value="row.property"
                    ></s-select>

                    <s-select v-if="row.condition === ''" class="" id="operationSelect" style="width: 200px; height: 32px; display: inline-block; vertical-align: bottom;
                        margin-right: 0.5em;"
                              :config="operationSelect" @value="(val) => row.operation=val"
                              :value="row.operation"
                    ></s-select>

                    <b-form-input v-if="row.condition === ''" v-model="row.value" size="sm" style="display: inline-block; width: 200px; padding-top: 1px;"></b-form-input>
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
    import SSelect from "../components/Select";

    export default {
        components: {SSelect},
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
                }
            }
        },
        computed: {
            isCleanActive() {
                return this.rows.length !== 0;
            },
        },
        methods: {
            addCondition() {
                this.rows.push({key: this.uuidv4(), condition: ''});
            },
            addRow(key) {
                let index = this.rows.findIndex((e) => e.key === key);
                if (index >= 0) {
                    this.rows.splice(index, 0, {key: this.uuidv4(), condition: ''});
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