<template>
    <div>
        <div v-if="errorMessage" class="s-dictionary-error-message">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-dictionary-loading s-dictionary-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="s-dictionary-container">

            <b-form-group
                    class="s-form-field"
                    :label="'Текст поручения'"
                    label-for="note-input"
                    >
                <b-form-textarea
                        id="note-input"
                        v-model="note"
                        />
                <b-form-invalid-feedback>

                </b-form-invalid-feedback>
            </b-form-group>

            <b-form-group
                    v-if="!addTasks"
                    label="Ответственный исполнитель"
                    label-for="main-executor-input">
                <s-select
                          id="main-executor-input" :config="userSelectConfig"
                          @value="(val) => this.mainExecutor = val"
                          :value="mainExecutor"></s-select>
            </b-form-group>

            <b-row>
                <b-col>
                    <b-form-group
                            label="Контролер"
                            label-for="controller-input">
                        <s-select
                                id="controller-input" :config="userSelectConfig"
                                @value="(val) => {this.controller = val.val; this.controllerTitle = val.title}"
                                :emit-with-title="true"
                                :value="controller"></s-select>
                    </b-form-group>
                </b-col>
                <b-col>
                    <b-form-group
                            label="Срок исполнения"
                            label-for="execution-date-input">
                        <date-picker
                                id="execution-date-input"
                                v-model="executionDate"
                                :config="dateConfig"/>
                    </b-form-group>
                </b-col>
            </b-row>

            <h5>Исполнители</h5>
            <s-table :buttons="buttons" :loadData="loadData" ref="executors-table"
                     :columns="tableColumns"
                     :delete-url="`delete/files`"
                     :add-url="`${url}`"
                     :add-type="`${addType}`"
                     :object-id="documentId"
                     :object-type="'executors'"
                     :no-delete-confirmation="true"
                     :delete-callback="deleteUsers"
                     :add-callback="addUser"
            ></s-table>

<!--            <h5>Вложения</h5>-->
<!--            <s-attach-form :object-id="objectId" :object-type="'document'"></s-attach-form>-->
        </div>
    </div>
</template>

<style>

</style>

<script>
    import axios from 'axios';
    import uuid from '../uuid.js';
    import SAttachForm from "../components/AttachForm";
    import SSelect from "./fields/Select";
    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        name: 's-execution-form',
        components: {
            STable: () => import('../components/Table'),
            SAttachForm,
            SSelect,
            datePicker
        },
        props: {
            documentId: {},
            actionId: {},
            addTasks: {
                default: false
            }
        },
        mounted() {
            this.loading = false;
        },
        methods: {
            loadData(spec) {
                let selfData = this.data;
                return new Promise(function(resolve, reject) {
                    resolve({data: {content: selfData}});
                });
            },
            deleteUsers(items) {
                items.forEach(item => {
                    this.data.splice(this.data.findIndex(o => o.id === item.id), 1);
                });
            },
            addUser() {
                this.data.push({
                    id: uuid(),
                    executor: null,
                    controller: this.controller,
                    controllerTitle: this.controllerTitle,
                    executionDate: this.executionDate ? new Date(this.executionDate) : null,
                    selected: false
                })
            },
            getData() {
                let executors = this.$refs['executors-table'].getData();
                if (executors) {
                    executors.forEach(e => e.executionDate = e.executionDate ? new Date(e.executionDate).toISOString() : null)
                }
                return {
                    documentId: this.documentId,
                    actionId: this.form,
                    note: this.note,
                    mainExecutor: this.mainExecutor,
                    controller: this.controller,
                    controllerTitle: this.controllerTitle,
                    executionDate: this.executionDate ? new Date(this.executionDate).toISOString() : null,
                    executors: executors
                }
            },
            getFormState() {
                return null;
            }
        },
        data() {
            return {
                data: [],
                loading: true,
                items: null,
                errorMessage: null,
                url: 'attaches/search/attachesByObjectId',
                addType: 'attach',

                note: "",
                mainExecutor: null,
                controller: null,
                controllerTitle: null,
                executionDate: null,

                buttons: {
                    pages: false,
                    refresh: false,
                    columns: false,
                    add: true,
                    del: true
                },
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
                        id: "executor",
                        title: "Исполнитель",
                        visible: true,
                        "type": "userSelect"
                    },
                    {
                        id: "controller",
                        title: "Контролер",
                        visible: true,
                        "type": "userSelect"
                    },
                    {
                        id: "executionDate",
                        title: "Срок исполнения",
                        visible: true,
                        "type": "dateSelect",
                        width: '200px'
                    }
                ],
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
                dateConfig: {
                    locale:'ru'
                }
            }
        }
    }
</script>