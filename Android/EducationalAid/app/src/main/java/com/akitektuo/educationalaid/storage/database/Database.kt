package com.akitektuo.educationalaid.storage.database

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Akitektuo on 01.02.2018.
 */

class Database {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage: StorageReference = FirebaseStorage.getInstance().reference

    init {
        database.keepSynced(true)
    }

    data class User(
            var name: String = "",
            val email: String = "",
            var image: String = "",
            var level: Int = 1,
            var currentXp: Int = 0,
            val admin: Boolean = false,
            var id: String = "")

    data class UserLesson(
            val userId: String = "",
            val lessonId: String = "",
            var admin: Boolean = false,
            val paid: Boolean = false,
            var started: Boolean = false,
            var completed: Boolean = false,
            var id: String = "")

    /**
     * @property type
     * 0 -> User joined Learning aid
     * 1 -> User started following x
     * 2 -> User stopped following x
     * 3 -> User created lesson x
     * 4 -> User started lesson x
     * 5 -> User completed lesson x
     * 6 -> User reached level x
     */
    data class Action(
            val userId: String = "",
            val type: Int = 0,
            val message: String = "",
            val date: Long = 0,
            var id: String = "")

    data class UserFollower(
            val userId: String = "",
            val followerId: String = "",
            var id: String = "")

    /**
     * @property visibility
     * 0 -> private
     * 1 -> protected
     * 2 -> public
     */
    data class Lesson(
            val name: String = "",
            val visibility: Int = 0,
            var image: String = "",
            var price: Double = 0.0,
            var id: String = "")

    data class UserStatus(
            val userId: String = "",
            val statusId: String = "",
            var status: Int = 0,
            var id: String = "")

    /**
     * @property status
     * 0 -> locked
     * 1 -> unlocked
     * 2 -> completed
     */
    data class Chapter(
            val lessonId: String = "",
            val name: String = "",
            var image: String = "",
            var imageLocked: String = "",
            val position: Int = 0,
            var id: String = "")

    /**
     * @property status
     * 0 -> locked
     * 1 -> unlocked
     * 2 -> completed
     */
    data class Module(
            val chapterId: String = "",
            val name: String = "",
            val position: Int = 0,
            var id: String = "")

    data class UserMIQ(
            val userId: String = "",
            val moduleIQId: String = "",
            var locked: Boolean = true,
            var id: String = "")

    data class ModuleIQ(
            val moduleId: String = "",
            val questionId: String = "",
            val infoId: String = "",
            val question: Boolean = false,
            val position: Int = 0,
            var id: String = "")

    data class Info(
            val title: String = "",
            val content: String = "",
            var image: String = "",
            val importance: String = "",
            var id: String = "")

    /**
     * @property type
     * 0 -> fill in
     * 1 -> single choice
     * 2 -> multiple choice
     * 3 -> drag in order
     * 4 -> drag and drop
     */
    data class Question(
            val task: String = "",
            val solving: String = "",
            val position: Int = 0,
            val type: Int = 0,
            var id: String = "")

    val databaseUsers = database.child("Users")
    private val databaseUsersLessons = database.child("UsersLessons")
    val databaseActions = database.child("Actions")
    private val databaseUsersFollowers = database.child("UsersFollowers")
    val databaseLessons = database.child("Lessons")!!
    val databaseChapters = database.child("Chapters")
    val databaseModules = database.child("Modules")
    val databaseUsersStatus = database.child("UsersStatus")
    private val databaseModulesIsQs = database.child("ModulesIsQs")
    val databaseUsersMsIsQs = database.child("UsersMsIsQs")
    private val databaseInfos = database.child("Infos")
    private val databaseQuestions = database.child("Questions")
    private val storageUsers = storage.child("Users")
    private val storageLessons = storage.child("Lessons")
    private val storageChapters = storage.child("Chapters")
    private val storageInfos = storage.child("Infos")

    fun addUserMIQ(userMIQ: UserMIQ) {
        if (userMIQ.id.isEmpty()) {
            userMIQ.id = databaseUsersMsIsQs.push().key
        }
        databaseUsersMsIsQs.child(userMIQ.id).setValue(userMIQ)
    }

    fun addUser(user: User, image: Uri? = null, afterResult: (profileUpdate: UserProfileChangeRequest) -> Unit = {}) {
        if (user.id.isEmpty()) {
            user.id = databaseUsers.push().key
        }
        addAction(Action(user.id, 0, "", Date().time))
        bindUserWithLessons(user.id)
        if (image == null) {
            databaseUsers.child(user.id).setValue(user)
        } else {
            storageUsers.child(user.id).putFile(image).addOnSuccessListener {
                user.image = it.downloadUrl.toString()
                databaseUsers.child(user.id).setValue(user)
                val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(user.name)
                        .setPhotoUri(it.downloadUrl)
                        .build()
                afterResult(profileUpdate)
            }
        }

    }

    fun bindUserWithLessons(userId: String) {
        databaseLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val lessons = ArrayList<Lesson>()
                data?.children?.mapNotNullTo(lessons, { it.getValue(Lesson::class.java) })
                lessons.filter { it.visibility != 2 }.forEach { lessons.remove(it) }
                lessons.forEach {
                    addUserLesson(UserLesson(userId, it.id, false, it.price == 0.0, false, false))
                    getChapterAll(it) {
                        it.forEach {
                            val chapter = it
                            if (chapter.position == 1) {
                                addUserStatus(UserStatus(userId, it.id, 1))
                            } else {
                                addUserStatus(UserStatus(userId, it.id, 0))
                            }
                            getModuleAll(chapter.id) {
                                it.forEach {
                                    val module = it
                                    if (chapter.position == 1 && module.position == 1) {
                                        addUserStatus(UserStatus(userId, it.id, 1))
                                    } else {
                                        addUserStatus(UserStatus(userId, it.id, 0))
                                    }
                                    getModuleIQAll(module.id) {
                                        it.forEach {
                                            if (chapter.position == 1 && module.position == 1 && it.position == 1) {
                                                addUserMIQ(UserMIQ(userId, it.id, false))
                                            } else {
                                                addUserMIQ(UserMIQ(userId, it.id, true))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun addUserStatus(userStatus: UserStatus) {
        if (userStatus.id.isEmpty()) {
            userStatus.id = databaseUsersStatus.push().key
        }
        databaseUsersStatus.child(userStatus.id).setValue(userStatus)
    }

    fun getUserStatus(userId: String, statusId: String, afterResult: (userStatus: UserStatus) -> Unit) {
        databaseUsersStatus.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersStatus = ArrayList<UserStatus>()
                data?.children?.mapNotNullTo(usersStatus, { it.getValue(UserStatus::class.java) })
                usersStatus.filter { it.userId != userId || it.statusId != statusId }.forEach { usersStatus.remove(it) }
                afterResult(usersStatus[0])
            }
        })
    }

    fun editUser(user: User, onCompleteListener: (task: Task<Void>, user: User) -> Unit = { _, _ -> }, image: Uri? = null) {
        if (image == null) {
            databaseUsers.child(user.id).setValue(user).addOnCompleteListener({
                onCompleteListener(it, user)
            })
        } else {
            storageUsers.child(user.id).putFile(image).addOnSuccessListener {
                user.image = it.downloadUrl.toString()
                databaseUsers.child(user.id).setValue(user).addOnCompleteListener({
                    onCompleteListener(it, user)
                })
            }
        }
    }

    fun getUser(id: String, afterResult: (user: User) -> Unit) {
        databaseUsers.child(id).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot?) {
                val user = data?.getValue(User::class.java)
                if (user != null) {
                    afterResult(user)
                }
            }

            override fun onCancelled(error: DatabaseError?) {

            }

        })
    }

    fun isNewUser(id: String, afterResult: (isNew: Boolean) -> Unit) {
        databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val users = ArrayList<User>()
                data?.children?.mapNotNullTo(users, {
                    it.getValue<User>(User::class.java)
                })
                var isNew = true
                users.filter { it.id == id }
                        .forEach { isNew = false }
                afterResult(isNew)
            }
        })
    }

    fun addUserLesson(userLesson: UserLesson): String {
        if (userLesson.id.isEmpty()) {
            userLesson.id = databaseUsersLessons.push().key
        }
        databaseUsersLessons.child(userLesson.id).setValue(userLesson)
        return userLesson.id
    }

    fun getUserLesson(id: String, afterResult: (userLesson: UserLesson) -> Unit) {
        databaseUsersLessons.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(UserLesson::class.java)!!)
            }
        })
    }

    fun getUserLessonAll(userId: String, afterResult: (usersLessons: ArrayList<UserLesson>) -> Unit) {
        databaseUsersLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersLessons = ArrayList<UserLesson>()
                data?.children?.mapNotNullTo(usersLessons, {
                    it.getValue<UserLesson>(UserLesson::class.java)
                })
                usersLessons.filter { it.userId != userId }.forEach { usersLessons.remove(it) }
                afterResult(usersLessons)
            }
        })
    }

    fun getUserLessonAll(lesson: Lesson, afterResult: (usersLessons: ArrayList<UserLesson>) -> Unit) {
        databaseUsersLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersLessons = ArrayList<UserLesson>()
                data?.children?.mapNotNullTo(usersLessons, {
                    it.getValue<UserLesson>(UserLesson::class.java)
                })
                afterResult(usersLessons.filter { it.userId == lesson.id } as ArrayList<UserLesson>)
            }
        })
    }

    fun getUserLessonAll(userId: String, lessonId: String, afterResult: (userLesson: UserLesson) -> Unit) {
        databaseUsersLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersLessons = ArrayList<UserLesson>()
                data?.children?.mapNotNullTo(usersLessons, {
                    it.getValue<UserLesson>(UserLesson::class.java)
                })
                usersLessons.filter { it.userId != userId || it.lessonId != lessonId }.forEach { usersLessons.remove(it) }
                afterResult(usersLessons[0])
            }
        })
    }

    fun addAction(action: Action): String {
        if (action.id.isEmpty()) {
            action.id = databaseActions.push().key
        }
        databaseActions.child(action.id).setValue(action)
        return action.id
    }

    fun getAction(id: String, afterResult: (action: Action) -> Unit) {
        databaseActions.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {
            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Action::class.java)!!)
            }
        })
    }

    fun getActionAll(userId: String, afterResult: (actions: ArrayList<Action>) -> Unit) {
        databaseActions.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val actions = ArrayList<Action>()
                data?.children?.mapNotNullTo(actions, {
                    it.getValue<Action>(Action::class.java)
                })
                actions.filter { it.userId != userId && it.message != userId }.forEach { actions.remove(it) }
                afterResult(actions)
            }
        })
    }

    fun addUserFollower(userFollower: UserFollower): String {
        if (userFollower.id.isEmpty()) {
            userFollower.id = databaseUsersFollowers.push().key
        }
        databaseUsersFollowers.child(userFollower.id).setValue(userFollower)
        return userFollower.id
    }

    fun getUserFollower(id: String, afterResult: (userFollower: UserFollower) -> Unit) {
        databaseUsersFollowers.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(UserFollower::class.java)!!)
            }
        })
    }

    fun getUserFollower(userId: String, otherUserId: String, afterResult: (usersFollowers: ArrayList<UserFollower>) -> Unit) {
        databaseUsersFollowers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersFollowers = ArrayList<UserFollower>()
                data?.children?.mapNotNullTo(usersFollowers, { it.getValue(UserFollower::class.java) })
                usersFollowers.filter { userId != it.followerId || otherUserId != it.userId }.forEach { usersFollowers.remove(it) }
                afterResult(usersFollowers)
            }
        })
    }

    fun getUserFollowerAll(user: User, afterResult: (usersFollowers: ArrayList<UserFollower>) -> Unit) {
        databaseUsersFollowers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersFollowers = ArrayList<UserFollower>()
                data?.children?.mapNotNullTo(usersFollowers, {
                    it.getValue<UserFollower>(UserFollower::class.java)
                })
                afterResult(usersFollowers.filter { it.userId == user.id } as ArrayList<UserFollower>)
            }
        })
    }

    fun isLessonAvailableForUser(userId: String, lessonId: String, afterResult: (paid: Boolean) -> Unit) {
        databaseUsersLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersLessons = ArrayList<UserLesson>()
                data?.children?.mapNotNullTo(usersLessons, {
                    it.getValue(UserLesson::class.java)
                })
                usersLessons.filter { it.userId == userId && it.lessonId == lessonId }
                        .forEach { afterResult(it.paid || it.admin) }

            }
        })
    }

    fun isChapterAvailableForLesson(chapterId: String, lessonId: String, afterResult: () -> Unit) {
        databaseChapters.child(chapterId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chapter = data?.getValue(Chapter::class.java)
                if (chapter?.lessonId == lessonId) {
                    afterResult()
                }
            }
        })
    }

    fun isModuleAvailableForChapter(moduleId: String, chapterId: String, afterResult: () -> Unit) {
        databaseModules.child(moduleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val module = data?.getValue(Module::class.java)
                if (module?.chapterId == chapterId) {
                    afterResult()
                }
            }
        })
    }

    fun getUserFollowerAll(lesson: Lesson, afterResult: (usersFollowers: ArrayList<UserFollower>) -> Unit) {
        databaseUsersFollowers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersFollowers = ArrayList<UserFollower>()
                data?.children?.mapNotNullTo(usersFollowers, {
                    it.getValue<UserFollower>(UserFollower::class.java)
                })
                afterResult(usersFollowers.filter { it.userId == lesson.id } as ArrayList<UserFollower>)
            }
        })
    }

    fun addLesson(userId: String, lesson: Lesson, image: Uri? = null, afterResult: () -> Unit = {}): String {
        if (lesson.id.isEmpty()) {
            lesson.id = databaseLessons.push().key
        }
        if (image == null) {
            databaseLessons.child(lesson.id).setValue(lesson)
            generateUsersLessons(userId, lesson, afterResult)
        } else {
            storageLessons.child(lesson.id).putFile(image).addOnSuccessListener {
                lesson.image = it.downloadUrl.toString()
                databaseLessons.child(lesson.id).setValue(lesson)
                generateUsersLessons(userId, lesson, afterResult)
            }
        }
        return lesson.id
    }

    private fun generateUsersLessons(userId: String, lesson: Lesson, afterResult: () -> Unit = {}) {
        databaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val users = ArrayList<User>()
                data?.children?.mapNotNullTo(users, { it.getValue(User::class.java) })
                users.forEach {
                    addUserLesson(UserLesson(it.id, lesson.id, paid = true))
                }
                afterResult()
            }

        })
    }

    fun getLesson(id: String, afterResult: (lesson: Lesson) -> Unit) {
        databaseLessons.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Lesson::class.java)!!)
            }
        })
    }

    fun getLessons(afterResult: (lessons: ArrayList<Lesson>) -> Unit) {
        databaseLessons.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val lessons = ArrayList<Lesson>()
                data?.children?.mapNotNullTo(lessons, { it.getValue(Lesson::class.java) })
                afterResult(lessons)
            }

        })
    }

    fun addChapter(chapter: Chapter, image: Uri? = null, imageLocked: Uri? = null, afterResult: () -> Unit = {}): String {
        if (chapter.id.isEmpty()) {
            chapter.id = databaseChapters.push().key
        }
        if (image == null || imageLocked == null) {
            databaseChapters.child(chapter.id).setValue(chapter)

        } else {
            storageChapters.child(chapter.id).putFile(image).addOnSuccessListener {
                chapter.image = it.downloadUrl.toString()
                storageChapters.child("${chapter.id}_locked").putFile(imageLocked).addOnSuccessListener {
                    chapter.imageLocked = it.downloadUrl.toString()
                    databaseChapters.child(chapter.id).setValue(chapter)
                    afterResult()
                }
            }
        }
        return chapter.id
    }

    fun getChapter(id: String, afterResult: (chapter: Chapter) -> Unit) {
        databaseChapters.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Chapter::class.java)!!)
            }
        })
    }

    fun getChapterAll(lesson: Lesson, afterResult: (chapters: ArrayList<Chapter>) -> Unit) {
        databaseChapters.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chapters = ArrayList<Chapter>()
                data?.children?.mapNotNullTo(chapters, {
                    it.getValue<Chapter>(Chapter::class.java)
                })
                chapters.filter { it.lessonId != lesson.id }.sortedBy { it.position }.forEach { chapters.remove(it) }
                afterResult(chapters)
            }
        })
    }

    fun addModule(module: Module): String {
        if (module.id.isEmpty()) {
            module.id = databaseModules.push().key
        }
        databaseModules.child(module.id).setValue(module)
        return module.id
    }

    fun getModule(id: String, afterResult: (module: Module) -> Unit) {
        databaseModules.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Module::class.java)!!)
            }
        })
    }

    fun getModuleAll(chapterId: String, afterResult: (modules: ArrayList<Module>) -> Unit) {
        databaseModules.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val modules = ArrayList<Module>()
                data?.children?.mapNotNullTo(modules, {
                    it.getValue<Module>(Module::class.java)
                })
                modules.filter { it.chapterId != chapterId }.sortedBy { it.position }.forEach { modules.remove(it) }
                afterResult(modules)
            }
        })
    }

    fun addInfo(info: Info, image: Uri? = null, afterResult: () -> Unit = {}): String {
        if (info.id.isEmpty()) {
            info.id = databaseInfos.push().key
        }
        if (image == null) {
            databaseInfos.child(info.id).setValue(info)
        } else {
            storageInfos.child(info.id).putFile(image).addOnSuccessListener {
                info.image = it.downloadUrl.toString()
                databaseInfos.child(info.id).setValue(info)
                afterResult()
            }
        }
        return info.id
    }

    fun getInfo(id: String, afterResult: (info: Info) -> Unit) {
        databaseInfos.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Info::class.java)!!)
            }
        })
    }

    fun addQuestion(question: Question): String {
        if (question.id.isEmpty()) {
            question.id = databaseQuestions.push().key
        }
        databaseQuestions.child(question.id).setValue(question)
        return question.id
    }

    fun getQuestion(id: String, afterResult: (question: Question) -> Unit) {
        databaseQuestions.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(Question::class.java)!!)
            }
        })
    }

    fun addModuleIQ(moduleIQ: ModuleIQ): String {
        if (moduleIQ.id.isEmpty()) {
            moduleIQ.id = databaseModulesIsQs.push().key
        }
        databaseModulesIsQs.child(moduleIQ.id).setValue(moduleIQ)
        return moduleIQ.id
    }

    fun editModuleIQ(moduleIQ: ModuleIQ, onCompleteListener: OnCompleteListener<Void> = OnCompleteListener { }) {
        databaseModulesIsQs.child(moduleIQ.id).setValue(moduleIQ).addOnCompleteListener(onCompleteListener)
    }

    fun getModuleIQ(id: String, afterResult: (moduleIQ: ModuleIQ) -> Unit) {
        databaseModulesIsQs.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(ModuleIQ::class.java)!!)
            }
        })
    }

    fun getModuleIQAll(moduleId: String, afterResult: (modulesIsQs: ArrayList<ModuleIQ>) -> Unit) {
        databaseModulesIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val modulesIsQs = ArrayList<ModuleIQ>()
                data?.children?.mapNotNullTo(modulesIsQs, {
                    it.getValue(ModuleIQ::class.java)
                })
                modulesIsQs.filter { it.moduleId != moduleId }.sortedBy { it.position }.forEach { modulesIsQs.remove(it) }
                afterResult(modulesIsQs)
            }
        })
    }

    fun getModelIQAll(userId: String, moduleId: String, afterResult: (modelsIsQs: ArrayList<ModuleIQ>) -> Unit) {
        databaseModulesIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                usersMsIsQs.filter { it.userId != userId }.forEach { usersMsIsQs.remove(it) }
                databaseModulesIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {
                    }

                    override fun onDataChange(data: DataSnapshot?) {
                        val modulesIsQsTemp = ArrayList<ModuleIQ>()
                        val modulesIsQs = ArrayList<ModuleIQ>()
                        data?.children?.mapNotNullTo(modulesIsQsTemp, { it.getValue(ModuleIQ::class.java) })
                        for (userMIQ in usersMsIsQs) {
                            modulesIsQsTemp.filter { it.id == userMIQ.moduleIQId && it.moduleId == moduleId }.forEach { modulesIsQs.add(it) }
                        }
                        afterResult(modulesIsQs)
                    }

                })
            }
        })
    }

    fun getUserMIQForLesson(userId: String, lesson: Lesson, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseChapters.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chaptersTemp = ArrayList<Chapter>()
                val chapters = ArrayList<Chapter>()
                data?.children?.mapNotNullTo(chaptersTemp, { it.getValue(Chapter::class.java) })
                chaptersTemp.filter { it.lessonId == lesson.id }.sortedBy { it.position }.forEach { chapters.add(it) }
                databaseModules.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {

                    }

                    override fun onDataChange(data: DataSnapshot?) {
                        val modulesTemp = ArrayList<Module>()
                        val modules = ArrayList<Module>()
                        data?.children?.mapNotNullTo(modulesTemp, { it.getValue(Module::class.java) })
                        for (chapter in chapters) {
                            modulesTemp.filter { chapter.id == it.chapterId }
                                    .sortedBy { it.position }
                                    .forEach { modules.add(it) }
                        }
                        databaseModulesIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {

                            }

                            override fun onDataChange(data: DataSnapshot?) {
                                val modulesIsQs = ArrayList<ModuleIQ>()
                                val modulesIsQsTemp = ArrayList<ModuleIQ>()
                                data?.children?.mapNotNullTo(modulesIsQsTemp, { it.getValue(ModuleIQ::class.java) })
                                for (module in modules) {
                                    modulesIsQsTemp.filter { module.id == it.moduleId }.forEach { modulesIsQs.add(it) }
                                }
                                databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError?) {

                                    }

                                    override fun onDataChange(data: DataSnapshot?) {
                                        val usersMsIsQsTemp = ArrayList<UserMIQ>()
                                        val usersMsIsQs = ArrayList<UserMIQ>()
                                        data?.children?.mapNotNullTo(usersMsIsQsTemp, { it.getValue(UserMIQ::class.java) })
                                        for (module in modulesIsQs) {
                                            usersMsIsQsTemp.filter { module.id == it.moduleIQId && it.userId == userId }
                                                    .forEach {
                                                        usersMsIsQs.add(it)
                                                    }
                                        }
                                        afterResult(usersMsIsQs)
                                    }
                                })
                            }
                        })
                    }
                })
            }
        })
    }

    fun getModulesForChapter(chapter: Chapter, afterResult: (modules: ArrayList<Module>) -> Unit) {
        databaseModules.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val modules = ArrayList<Module>()
                data?.children?.mapNotNullTo(modules, { it.getValue(Module::class.java) })
                modules.filter { chapter.id != it.chapterId }
                        .sortedBy { it.position }
                        .forEach { modules.remove(it) }
                afterResult(modules)
            }
        })
    }

    fun editUserStatus(userStatus: UserStatus, onCompleteListener: (task: Task<Void>, userStatus: UserStatus) -> Unit = { _, _ -> }) {
        databaseUsersStatus.child(userStatus.id).setValue(userStatus).addOnCompleteListener({ onCompleteListener(it, userStatus) })
    }

    fun getUserMIQ(id: String, afterResult: (userMIQ: UserMIQ) -> Unit) {
        databaseUsersMsIsQs.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                afterResult(data?.getValue(UserMIQ::class.java)!!)
            }
        })
    }

    fun getUserMIQ(userId: String, moduleIQId: String, afterResult: (userMIQ: UserMIQ) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, { it.getValue(UserMIQ::class.java) })
                usersMsIsQs.filter { it.moduleIQId != moduleIQId || it.userId != userId }.forEach { usersMsIsQs.remove(it) }
                afterResult(usersMsIsQs[0])
            }
        })
    }

    fun getUserMIQAll(moduleIQId: String, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, { it.getValue(UserMIQ::class.java) })
                usersMsIsQs.filter { it.moduleIQId != moduleIQId }.forEach { usersMsIsQs.remove(it) }
                afterResult(usersMsIsQs)
            }
        })
    }

    fun editUserMIQ(userMIQ: UserMIQ) {
        databaseUsersMsIsQs.child(userMIQ.id).setValue(userMIQ)
    }

    fun unlockNext(userId: String, moduleIQId: String, afterResult: () -> Unit) {
        getModuleIQ(moduleIQId) {
            getModule(it.moduleId, {
                val currentModule = it
                getChapter(currentModule.chapterId, {
                    val currentChapter = it
                    getModuleAll(currentChapter.id, {
                        getUserStatus(userId, currentModule.id, {
                            it.status = 2
                            editUserStatus(it)
                        })
                        if (currentModule.position == it.size) {
                            getLesson(currentChapter.lessonId, {
                                getChapterAll(it, {
                                    //edit current chapter to finished
                                    getUserStatus(userId, currentChapter.id, {
                                        it.status = 2
                                        editUserStatus(it)
                                    })
                                    if (currentChapter.position < it.size) {
                                        val nextChapter = it[currentChapter.position + 1]
                                        getModuleAll(nextChapter.id, {
                                            if (it.size > 0) {
                                                val nextModule = it[0]
                                                getUserStatus(userId, nextModule.id, {
                                                    if (it.status == 0) {
                                                        it.status = 1
                                                        editUserStatus(it)
                                                    }
                                                })
                                                getModuleIQAll(nextModule.id) {
                                                    if (it.size > 0) {
                                                        getUserMIQ(userId, it[0].id) {
                                                            it.locked = false
                                                            editUserMIQ(it)
                                                            afterResult()
                                                        }
                                                    } else {
                                                        afterResult()
                                                    }
                                                }
                                            } else {
                                                afterResult()
                                            }
                                        })
                                    } else {
                                        afterResult()
                                    }
                                })
                            })
                        } else {
                            val nextModule = it[currentModule.position]
                            getUserStatus(userId, nextModule.id, {
                                if (it.status == 0) {
                                    it.status = 1
                                    editUserStatus(it)
                                }
                            })
                            getModuleIQAll(nextModule.id) {
                                if (it.size > 0) {
                                    getUserMIQ(userId, it[0].id) {
                                        it.locked = false
                                        editUserMIQ(it)
                                        afterResult()
                                    }
                                } else {
                                    afterResult()
                                }
                            }
                        }
                    })
                })
            })
        }
    }

    fun editUserLesson(userLesson: Database.UserLesson) {
        databaseUsersLessons.child(userLesson.id).setValue(userLesson)
    }

    fun removeUserFollower(id: String) {
        databaseUsersFollowers.child(id).removeValue()
    }

    fun getChapters(afterResult: (chapters: ArrayList<Chapter>) -> Unit) {
        databaseChapters.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chapters = ArrayList<Chapter>()
                data?.children?.mapNotNullTo(chapters, { it.getValue(Chapter::class.java) })
                afterResult(chapters)
            }

        })
    }

}