<template>
    <b-form-group
            label="Значение"
            label-for="attachInputValue">
        <b-form-file
                id="attachInputValue"
                v-model="modelValue.file"
                required />
    </b-form-group>
</template>

<script>
    export default {
        components: {},
        name: 's-add-attach',
        mounted() {

        },
        data() {
            return {

            }
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
            getFormState() {
                if (!this.modelValue.file) {
                    return ["Значение"];
                }
                return null;
            }
        },
        props: {
            value: {}
        }
    }
</script>

<style>
    
    
</style>