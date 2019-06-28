<template>
    <b-form-group
            label="Значение"
            label-for="dictionaryInputValue">
        <b-form-input
                id="dictionaryInputValue"
                v-model="modelValue"
                required />
    </b-form-group>
</template>

<script>
    export default {
        components: {},
        name: 's-dictionary-value',
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

        },
        props: {
            value: {}
        }
    }
</script>

<style>
    
    
</style>