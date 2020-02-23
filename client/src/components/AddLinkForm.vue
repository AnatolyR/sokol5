<template>
    <b-form-group
            label="Значение"
            label-for="linkInputValue">
        <b-form-input
                id="linkInputValue"
                v-model="modelValue.toDocument"/>
    </b-form-group>
</template>

<script>
    export default {
        components: {},
        name: 's-add-link',
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
                if (!this.modelValue.toDocument) {
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