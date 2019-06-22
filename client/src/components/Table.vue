<template>
    <div>
        <div>
            <div class="s-folder-buttons-bar">
                <b-button :disabled="!isPrevPage" size="sm" @click="prevPage"><font-awesome-icon icon="angle-left" /> Предыдущая</b-button>

                <b-dropdown id="dropdown-1" :text="currentPageItem" size="sm">
                    <b-dropdown-item v-for="item in pageItems" :key="item.title"
                                     :active="activePage(item.page)"
                                     boundary="viewport"
                                     @click="setPage(item.page)">{{item.title}}</b-dropdown-item>
                </b-dropdown>

                <b-button :disabled="!isNextPage" size="sm" @click="nextPage">Следующая <font-awesome-icon icon="angle-right" /></b-button>

                <b-dropdown id="dropdown-2" text="Колонки" size="sm">
                    <div class="dropdown-item" v-for="col in columns"><b-form-checkbox style="width: 100%;" v-model="col.visible" boundary="viewport">{{col.title}}</b-form-checkbox></div>
                </b-dropdown>

                <b-button size="sm" @click="update">Обновить</b-button>
                <b-button :variant="conditions && conditions.length > 0 ? 'warning' : ''" size="sm" @click="toggleFilter">Фильтр</b-button>

            </div>
        </div>
        <keep-alive>
            <s-filter v-if="displayFilter" :propertySelect="propertySelect()" @update="update" @value="(val) => this.conditions = val"></s-filter>
        </keep-alive>
        <table class="table table-bordered table-sm">
            <thead>
            <tr>
                <th v-for="col in visibleColumns" scope="col" @click="doSort(col.id)">{{col.title}}
                    <font-awesome-icon v-if="sortProperty === col.id && sortDirection" :icon="sortDirectionAngle" />
                </th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in data">
                <td v-for="col in visibleColumns">
                    <span v-if="!col.type">{{item[col.id]}}</span>
                    <router-link v-if="col.type === 'link'"
                                 :to="col.path + item.id">{{ item[col.id] }}</router-link>
                </td>
            </tr>
            </tbody>
        </table>
        <div>
            <div class="s-folder-buttons-bar">
                <b-button :disabled="!isPrevPage" size="sm" @click="prevPage"><font-awesome-icon icon="angle-left" /> Предыдущая</b-button>


                <b-dropdown id="dropdown-3" :text="currentPageItem" size="sm">
                    <b-dropdown-item v-for="item in pageItems" :key="item.title"
                                     :active="activePage(item.page)"
                                     boundary="viewport"
                                     @click="setPage(item.page)">{{item.title}}</b-dropdown-item>
                </b-dropdown>

                <b-button :disabled="!isNextPage" size="sm" @click="nextPage">Следующая <font-awesome-icon icon="angle-right" /></b-button>

            </div>
        </div>
    </div>
</template>

<style>
    .s-folder-buttons-bar {
        margin-bottom: 0.5em;
    }
    .s-folder-buttons-bar button {
        margin-right: 0.5em;
    }
    .s-folder-buttons-bar .dropdown {
        /*margin-right: 0.5em;*/
    }
    .s-folder-buttons-bar .btn-group {

    }
    .s-folder-buttons-bar .dropdown-menu {
        min-width: unset;
        max-height: 400px;
        overflow: scroll;
    }
</style>

<script>
    import SFilter from "./Filter";
    export default {
        name: 's-table',
        components: {SFilter},
        mounted() {
            this.update();
        },
        computed: {
            visibleColumns() {
                return this.columns.filter((c) => c.visible);
            },
            items() {
                return this.data;
            },
            isNextPage() {
                return this.page < this.totalPages - 1;
            },
            isPrevPage() {
                return this.page > 0;
            },
            pageItems() {
                let selectors = [];
                for (let p = 1; p <= this.totalPages; p++) {
                    selectors.push({
                        page: p - 1,
                        title: p + " / " + this.totalPages
                    });
                }
                return selectors;
            },
            currentPageItem() {
                return (this.page + 1) + " / " + this.totalPages;
            },
            sortDirectionAngle() {
                if (this.sortDirection === "DESC") {
                    return "angle-down";
                } else if (this.sortDirection === "ASC") {
                    return "angle-up";
                } else {
                    return null;
                }
            }
        },
        props: {
            loadData: {
                type: Function
            },
            columns: {}
        },
        methods: {
            update() {
                this.loadData({size: this.size, page: this.page,
                        sortDirection: this.sortDirection, sortProperty: this.sortProperty,
                        conditions: this.conditions}).then((res) => {
                    let fullData = res.data;
                    this.data = fullData.content;
                    this.totalPages = fullData.totalPages;
                });
            },
            nextPage() {
                if (this.isNextPage) {
                    this.page = this.page + 1;
                }
            },
            prevPage() {
                if (this.isPrevPage) {
                    this.page = this.page - 1;
                }
            },
            setPage(page) {
                this.page = page;
            },
            activePage(page) {
                return page === this.page;
            },
            doSort(sortProperty) {
                if (this.sortProperty != null && this.sortProperty !== sortProperty) {
                    this.sortDirection = null;
                }

                this.sortProperty = sortProperty;
                if (this.sortDirection == null) {
                    this.sortDirection = "DESC";
                } else if (this.sortDirection === "DESC") {
                    this.sortDirection = "ASC";
                } else if (this.sortDirection === "ASC") {
                    this.sortDirection = null;
                    this.sortProperty = null;
                }
                this.update();
            },
            propertySelect() {
                return {
                    maxItems: 1,
                    valueField: 'id',
                    labelField: 'title',
                    preload: true,
                    options: this.columns,
                };
            },
            toggleFilter() {
                this.displayFilter = !this.displayFilter;
            }
        },
        watch: {
            page() {
                this.update();
            }
        },
        data() {
            return {
                size: 20,
                page: 0,
                totalPages: null,
                totalElements: null,
                data: [],
                sortProperty: null,
                sortDirection: null,
                conditions: null,
                displayFilter: false
            }
        }
    }
</script>