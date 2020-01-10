<template>
    <s-date-group
            :title="title"
            :id="name + 'Input'"
            :fieldLevel="dataSource.fieldsLevels[name]"
            :editMode="dataSource.editMode"
            v-model="dataSource.document[name]"
            :valueTitle="dataSource.document[name + 'Str'] || dataSource.document[name]"
            :errorMessage="title + ' не может быть пустой'"
            :state="() => dataSource.state(name)"
    ></s-date-group>
</template>

<style>

</style>

<script>
    import SDateGroup from "./DateGroup"

    export default {
        components: {SDateGroup},
        name: "s-date-field",
        props: {
            name: {},
            title: {},
        },
        computed: {
            dataSource() {return this.$parent.dataSource;}
        },
        methods: {

        }
    }
</script>