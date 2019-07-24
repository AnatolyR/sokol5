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
                <b-button v-if="buttons.filter" :variant="this.displayFilter ? 'outline-secondary' : (conditions && conditions.length > 0 ? 'warning' : '')" size="sm" @click="toggleFilter">Фильтр</b-button>
                <b-button v-if="buttons.add" variant="success" size="sm" @click="showAddModal">Добавить</b-button>
                <b-button v-if="buttons.del" :disabled="toDeleteItems === ''" variant="danger" size="sm" @click="showDelModal">Удалить</b-button>

                <b-modal id="add-modal" ref="add-modal" title="Добавление" ok="'Сохранить'" cancel="'Отмена'">
                    <p class="my-4">

                        <s-dictionary-value v-if="addType === 'string'" v-model="toAddValue" ref="addForm"></s-dictionary-value>

                        <s-dictionary-value v-if="addType === 'role'" v-model="toAddValue" ref="addForm"></s-dictionary-value>

                        <s-user-form v-if="addType === 'user'" v-model="toAddValue" :edit-mode="true" ref="addForm"></s-user-form>

                        <s-contragent-form v-if="addType === 'contragent'" v-model="toAddValue" :edit-mode="true" ref="addForm"></s-contragent-form>

                        <s-add-attach v-if="addType === 'attach'" v-model="toAddValue" ref="addForm"></s-add-attach>
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-form-checkbox v-model="addOneMore"
                        >
                            Добавить еще одно значение
                        </b-form-checkbox>
                        <b-button size="sm" variant="light" @click="cancel()">
                            Отмена
                        </b-button>
                        <b-button size="sm" variant="success" @click="addValue()">
                            Сохранить
                        </b-button>
                    </template>
                </b-modal>
                <b-toast id="add-success-toast" title="Добавление" static no-auto-hide>
                    Значение успешно добавлено
                </b-toast>

                <b-modal id="info-modal" ref="info-modal" modal-class="s-modal-incorrect-fields" title="Некорректно заполненные поля">
                    <p class="my-4">
                        {{uncorrectFields}}
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-button size="sm" variant="info" @click="cancel()">
                            Ок
                        </b-button>
                    </template>
                </b-modal>

                <b-modal id="del-modal" ref="del-modal" title="Подтверждение удаления" modal-class="s-modal-confirm-delete">
                    <p class="my-4">Будут удалены значения:
                    {{toDeleteItems}}
                    </p>
                    <template slot="modal-footer" slot-scope="{ ok, cancel, hide }">
                        <b-button size="sm" variant="light" @click="cancel()">
                            Отмена
                        </b-button>
                        <b-button size="sm" variant="danger" @click="deleteSelected()">
                            Удалить
                        </b-button>
                    </template>
                </b-modal>
            </div>
        </div>
        <keep-alive>
            <s-filter v-if="displayFilter" :propertySelect="propertySelect()" @update="update" @value="(val) => this.conditions = val"></s-filter>
        </keep-alive>
        <table class="table table-bordered table-sm">
            <thead>
            <tr>
                <th v-for="col in visibleColumns" scope="col" @click="doSort(col.id)" :style="col.type === 'checkbox' ? 'width: 2em;' : ''">
                    <span v-if="col.type !== 'checkbox'">
                        {{col.title}}
                        <font-awesome-icon v-if="sortProperty === col.id && sortDirection" :icon="sortDirectionAngle" />
                    </span>
                </th>
            </tr>
            </thead>
            <tbody>

            <!-- ========================== -->
            <tr v-for="item in data">
                <td v-for="col in visibleColumns"
                    @click="() => {if (col.type === 'checkbox') item.selected = !item.selected}">
                    <span v-if="col.type !== 'link'
                        && col.type !== 'checkbox'
                        && col.type !== 'fileLink'"
                          :class="`s-table-cell-${col.id}`">{{item[col.id]}}</span>
                    <router-link v-if="col.type === 'link'"
                                 :class="`s-table-cell-${col.id}`"
                                 :to="col.path + item.id">{{ item[col.id] }}</router-link>

                    <router-link v-if="col.type === 'fileLink'"
                                 :class="`s-table-cell-${col.id}`"
                                 target="_blank"
                                 :to="'/api/file/' + item.id">{{ item[col.id] }}</router-link>

                    <b-form-checkbox v-if="col.type === 'checkbox' && item.id"
                                     :class="`s-table-cell-${col.id}`"
                                     v-model="item.selected" @click.native="(e) => e.preventDefault()"></b-form-checkbox>
                </td>
            </tr>
            <!-- ========================== -->
            
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
    import axios from 'axios';
    import SDictionaryValue from './DictionaryValue';
    import SUserForm from "../components/UserForm";
    import SContragentForm from "../components/ContragentForm";
    import SAddAttach from "./AddAttachForm";

    export default {
        name: 's-table',
        components: {SAddAttach, SFilter, SDictionaryValue, SUserForm, SContragentForm},
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
                return (this.page + 1) + " / " + (this.totalPages ? this.totalPages : '') ;
            },
            sortDirectionAngle() {
                if (this.sortDirection === "DESC") {
                    return "angle-down";
                } else if (this.sortDirection === "ASC") {
                    return "angle-up";
                } else {
                    return null;
                }
            },
            toDeleteItems() {
                return this.data.filter((i) => i.selected === true).map((i) => i.title).join(", ");
            }
        },
        props: {
            loadData: {
                type: Function
            },
            columns: {},
            buttons: {
                default: function () {
                    return {}
                }
            },
            deleteUrl: {},
            addUrl: {},
            addType: {
                default: 'default'
            },
            objectId: {},
            objectType: {}
        },
        methods: {
            update() {
                this.loadData({size: this.size, page: this.page,
                        sortDirection: this.sortDirection, sortProperty: this.sortProperty,
                        conditions: this.conditions}).then((res) => {
                    let fullData = res.data;

                    if (this.buttons.del) {
                        fullData.content.forEach(i => i.selected = false);
                    }

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
            },
            showAddModal() {
                this.$refs['add-modal'].show();
            },
            hideAddModal() {
                this.$refs['add-modal'].hide();
            },
            toggleAddModal() {
                this.$refs['add-modal'].toggle('#toggle-btn');
            },
            showDelModal() {
                this.$refs['del-modal'].show();
            },
            deleteSelected() {
                this.$refs['del-modal'].hide();
                
                let ids = this.data.filter((i) => i.selected === true).map((i) => i.id);
                axios.delete(`/api/${this.deleteUrl}/${ids.join(',')}`).then((res) => {

                    this.$bvToast.toast(`Значение(я) успешно удалены`, {
                        variant: 'success',
                        solid: true,
                        autoHideDelay: 2000
                    });

                    this.update();
                }).catch((res) => {
                    this.$bvToast.toast(`Не удалось удалить значения`, {
                        variant: 'danger',
                        solid: true,
                        autoHideDelay: 4000
                    });
                });
            },
            addValue() {
                let fields = this.$refs.addForm && this.$refs.addForm.getFormState();
                if (fields && fields.length > 0) {
                    this.uncorrectFields = fields.join(", ");
                    this.$refs['info-modal'].show();
                    return;
                }

                if (this.addType === 'attach') {
                    let formData = new FormData();
                    formData.append('file', this.toAddValue.file);
                    formData.append('objectId', this.objectId);
                    formData.append('objectType', this.objectType);

                    axios.post('/api/file',
                        formData,
                        {
                            headers: {
                                'Content-Type': 'multipart/form-data'
                            }
                        }
                    ).then((res) => {
                        this.$bvToast.toast(`Значение успешно добавлено`, {
                            variant: 'success',
                            solid: true,
                            autoHideDelay: 2000
                        });

                        this.update();
                    })
                    .catch((e) => {
                        console.log(e);
                    });
                } else {
                    if (this.addType === 'role') {
                        this.toAddValue['userId'] = this.objectId;
                    }

                    axios.post(`/api/${this.addUrl}`, this.toAddValue).then((res) => {
                        this.$bvToast.toast(`Значение успешно добавлено`, {
                            variant: 'success',
                            solid: true,
                            autoHideDelay: 2000
                        });

                        this.update();
                    });
                }

                if (!this.addOneMore) {
                    this.$refs['add-modal'].hide();
                }
                if (this.$refs.addForm) {
                    this.$refs.addForm.highlightErrors = false;
                }
                this.toAddValue = {};
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
                displayFilter: false,
                toAddValue: {},
                addOneMore: false,
                uncorrectFields: null
            }
        }
    }
</script>