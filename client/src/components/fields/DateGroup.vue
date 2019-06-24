<template>
    <b-form-group
            :label="title"
            :label-for="id"
            v-if="fieldLevel > 0">
        <b-form-input
                v-if="!editMode || fieldLevel == 1"
                :id="id"
                v-model="valueTitle"
                required
                :readonly="!editMode || fieldLevel == 1"
                :plaintext="!editMode || fieldLevel == 1"  />
        <date-picker
                v-if="editMode && fieldLevel > 1"
                :id="id"
                :state="checkState()"
                v-model="modelValue"
                :config="dateConfig"/>
        <div v-if="editMode && fieldLevel > 1"
             class="invalid-feedback"
             v-bind:style="{display: checkState() === false ? 'block' : 'none'}">
            {{errorMessage}}
        </div>
    </b-form-group>
</template>

<style>

</style>

<script>
    import datePicker from 'vue-bootstrap-datetimepicker';

    export default {
        name: "s-date-group",
        components: {datePicker},
        data() {
            return {
                dateConfig: {
                    locale:'ru'
                }
            }
        },
        props: {
            title: {},
            id: {},
            fieldLevel: {},
            editMode: {},
            value: {},
            valueTitle: {},
            errorMessage: {},
            state: {type: Function},
        },
        computed: {
            modelValue: {
                get() {
                    return this.value;
                },
                set(v) {
                    this.$emit('input', v)
                }
            }
        },
        methods: {
            checkState() {
                return this.state ? this.state() : null;
            }
        }
    }
</script>