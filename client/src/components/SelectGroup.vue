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
        <s-select v-if="editMode && fieldLevel > 1"
                  :id="id" :config="selectConfig"
                  @value="(val) => modelValue=val"
                  :value="modelValue"
                  :depends="depends"
                  :valueTitle="valueTitle"
                  v-bind:class="{'s-invalid-field': checkState() === false ? true : false}"></s-select>
        <div v-if="editMode && fieldLevel > 1"
             class="invalid-feedback"
             v-bind:style="{display: checkState() === false ? 'block' : 'none'}">
            {{errorMessage}}
        </div>
    </b-form-group>
</template>

<style>
    .s-invalid-field .selectize-input {
        border-color: #dc3545 !important;
    }
</style>

<script>
    import SSelect from "../components/Select";

    export default {
        name: "s-select-group",
        components: {SSelect},
        props: {
            title: {},
            id: {},
            fieldLevel: {},
            editMode: {},
            value: {},
            valueTitle: {},
            errorMessage: {},
            selectConfig: {},
            state: {type: Function},
            depends: {}
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
                return this.state();
            }
        }
    }
</script>