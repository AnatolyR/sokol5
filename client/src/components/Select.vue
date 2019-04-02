<template>
    <div>
         <select ref="select"></select>
    </div>
</template>

<script>
    import $ from 'jquery';
    if (!$().selectize) {
        require('selectize')
    }

    export default {
        name: "s-select",
        mounted() {
            if (this.created) {
                return;
            }

            var $select = $(this.$refs["select"]).selectize(this.config);
            var selectize = $select[0].selectize;
            setTimeout(() => selectize.setValue(this.value), 10);

            selectize.on("change", (val) => {
                if (val !== this.value) {
                    this.$emit('value', val);
                }
            });

            this.created = true;
        },
        data() {
            return {
                created: false
            }
        },
        props: [
            'config',
            'value'
        ]
    }
</script>

