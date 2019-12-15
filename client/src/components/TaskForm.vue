<template>
    <div>
        <div v-if="errorMessage" class="s-dictionary-error-message">
            <b-alert show variant="danger">{{errorMessage}}</b-alert>
        </div>
        <div v-if="loading" class="s-dictionary-loading s-dictionary-loader">
            <b-spinner variant="secondary" label="Загрузка..." />
        </div>
        <div v-if="!loading" class="s-task-form">

            <b-form-group
                    class="s-form-field"
                    :label="'Текст поручения'"
                    label-for="note-input"
            >
                <b-form-textarea
                        id="note-input"
                        v-model="task.description"
                />
                <b-form-invalid-feedback>

                </b-form-invalid-feedback>
            </b-form-group>

            <b-row>
                <b-col>
                    <s-select-group
                            title="Исполнитель"
                            id="executor"
                            :fieldLevel="fieldsLevels.executor"
                            :editMode="editMode"
                            v-model="task.executor"
                            :valueTitle="task.executorTitle"
                            errorMessage="Исполнитель должен быть указан"
                            :state="() => state('executorTitle')"
                            :selectConfig="userSelectConfig"
                    ></s-select-group>
                </b-col>
                <b-col>
                    <b-form-group
                            label="Автор"
                            label-for="main-executor-input">
                        <span>{{task.authorTitle}}</span>
                    </b-form-group>
                </b-col>
            </b-row>

            <b-row>
                <b-col>
                    <s-select-group
                            title="Контроллер"
                            id="controller"
                            :fieldLevel="fieldsLevels.controller"
                            :editMode="editMode"
                            v-model="task.controllerId"
                            :valueTitle="task.controllerTitle"
                            errorMessage=""
                            :state="() => state('controller')"
                            :selectConfig="userSelectConfig"
                    ></s-select-group>
                </b-col>
                <b-col>
                    <b-form-group
                            label="Срок исполнения"
                            label-for="execution-date-input">
                        <date-picker
                                id="execution-date-input"
                                v-model="task.dueDate"
                                :config="dateConfig"/>
                    </b-form-group>
                </b-col>
            </b-row>
        </div>
    </div>
</template>

<style>
    .s-task-form {
        margin: 1em;
    }

    .s-task-form label {
        margin-bottom: 0;
        font-weight: bold;
    }
</style>

<script>
    import axios from 'axios';
    import SSelect from "./fields/Select";
    import SSelectGroup from "../components/fields/SelectGroup";
    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        name: 's-task-form',
        components: {SSelect, SSelectGroup, datePicker},
        mounted() {
            this.loadTask();
        },
        props: [
            "taskId"
        ],
        methods: {
            loadTask() {
                axios.get(`/api/tasks/${this.taskId}`)
                    .then((res) => {
                        this.task = res.data;
                        this.task.dueDate = new Date(this.task.dueDate);
                        this.loading = false;
                    });
            },
            state(field) {
                return null;
            }
        },
        data() {
            return {
                task: null,
                dateConfig: {
                    locale:'ru'
                },
                loading: true,
                errorMessage: null,
                editMode: false,
                fieldsLevels: {
                    executor: "2",
                    controller: "2"
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
                }
            }
        }
    }
</script>