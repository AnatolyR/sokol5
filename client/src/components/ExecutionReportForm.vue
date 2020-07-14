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
                    :label="'Текст отчета'"
                    label-for="note-input"
                    >
                <b-form-textarea
                        id="note-input"
                        v-model="note"
                        />
                <b-form-invalid-feedback>

                </b-form-invalid-feedback>
            </b-form-group>
        </div>
    </div>
</template>

<style>

</style>

<script>
    export default {
        name: 's-execution-report-form',
        components: {

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
            getData() {
                return {
                    documentId: this.documentId,
                    actionId: this.form,
                    note: this.note
                }
            },
            getFormState() {
                return null;
            }
        },
        data() {
            return {
                loading: true,
                items: null,
                errorMessage: null,
                note: "",
                mainExecutor: null,
                controller: null,
                controllerTitle: null,
                executionDate: null
            }
        }
    }
</script>