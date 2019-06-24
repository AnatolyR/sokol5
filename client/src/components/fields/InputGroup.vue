<template>
    <b-form-group
            :label="title"
            :label-for="id"
            v-if="fieldLevel > 0">
        <b-form-input
                :id="id"
                :state="checkState()"
                v-model="modelValue"
                required :readonly="!editMode || fieldLevel == 1"
                :plaintext="!editMode || fieldLevel == 1"  />
        <b-form-invalid-feedback>
            {{errorMessage}}
        </b-form-invalid-feedback>
    </b-form-group>
</template>

<style>

</style>

<script>
    export default {
        name: "s-input-group",
        props: {
            title: {},
            id: {},
            fieldLevel: {},
            editMode: {},
            value: {},
            errorMessage: {},
            state: {type: Function}
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