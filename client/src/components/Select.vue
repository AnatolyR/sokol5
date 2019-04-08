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

            const config = {...this.config};
            if (this.value) {
                config.options = [{id: this.value, title: this.valueTitle}];
            }

            var $select = $(this.$refs["select"]).selectize(config);
            var selectize = $select[0].selectize;
            setTimeout(() => selectize.setValue(this.value), 10);
            selectize.depends = this.depends;

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
            'value',
            'valueTitle',
            'depends'
        ]
    }
</script>

