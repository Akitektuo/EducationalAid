package com.akitektuo.educationalaid.storage.database

import android.net.Uri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
            val level: Int = 1,
            val currentXp: Int = 0,
            val isAdmin: Boolean = false,
            var id: String = "")

    data class UserLesson(
            val userId: String = "",
            val lessonId: String = "",
            val isAdmin: Boolean = false,
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
            val started: Boolean = false,
            var image: String = "",
            var price: Double = 0.0,
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
            val status: Int = 0,
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
            val status: Int = 0,
            var id: String = "")

    data class UserMIQ(
            val userId: String = "",
            val moduleId: String = "",
            val questionId: String = "",
            val infoId: String = "",
            val question: Boolean = false,
            val position: Int = 0,
            val locked: Boolean = true,
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
            val type: Int = 0,
            var id: String = "")

    private val databaseUsers = database.child("Users")
    private val databaseUsersLessons = database.child("UsersLessons")
    private val databaseActions = database.child("Actions")
    private val databaseUsersFollowers = database.child("UsersFollowers")
    val databaseLessons = database.child("Lessons")!!
    val databaseChapters = database.child("Chapters")
    val databaseModules = database.child("Modules")
    private val databaseUsersMsIsQs = database.child("ModulesIsQs")
    private val databaseInfos = database.child("Infos")
    private val databaseQuestions = database.child("Questions")
    private val storageUsers = storage.child("Users")
    private val storageLessons = storage.child("Lessons")
    private val storageChapters = storage.child("Chapters")
    private val storageInfos = storage.child("Infos")

    fun addUser(user: User, image: Uri? = null, afterResult: (profileUpdate: UserProfileChangeRequest) -> Unit = {}) {
        if (user.id.isEmpty()) {
            user.id = databaseUsers.push().key
        }
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

    fun editUser(user: User, onCompleteListener: (task: Task<Void>, user: User) -> Unit, image: Uri? = null) {
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
                afterResult(data?.getValue(User::class.java)!!)
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

    fun getUserLessonAll(user: User, afterResult: (usersLessons: ArrayList<UserLesson>) -> Unit) {
        databaseUsersLessons.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersLessons = ArrayList<UserLesson>()
                data?.children?.mapNotNullTo(usersLessons, {
                    it.getValue<UserLesson>(UserLesson::class.java)
                })
                afterResult(usersLessons.filter { it.userId == user.id } as ArrayList<UserLesson>)
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

    fun getActionAll(user: User, afterResult: (actions: ArrayList<Action>) -> Unit) {
        databaseActions.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val actions = ArrayList<Action>()
                data?.children?.mapNotNullTo(actions, {
                    it.getValue<Action>(Action::class.java)
                })
                afterResult(actions.filter { it.userId == user.id }.sortedByDescending { it.date } as ArrayList<Action>)
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

    fun addLesson(lesson: Lesson, image: Uri? = null, afterResult: () -> Unit = {}): String {
        if (lesson.id.isEmpty()) {
            lesson.id = databaseLessons.push().key
        }
        if (image == null) {
            databaseLessons.child(lesson.id).setValue(lesson)
        } else {
            storageLessons.child(lesson.id).putFile(image).addOnSuccessListener {
                lesson.image = it.downloadUrl.toString()
                databaseLessons.child(lesson.id).setValue(lesson)
                afterResult()
            }
        }
        return lesson.id
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

    fun getChaterAll(lesson: Lesson, afterResult: (chapters: ArrayList<Chapter>) -> Unit) {
        databaseChapters.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chapters = ArrayList<Chapter>()
                data?.children?.mapNotNullTo(chapters, {
                    it.getValue<Chapter>(Chapter::class.java)
                })
                afterResult(chapters.filter { it.lessonId == lesson.id }.sortedBy { it.position } as ArrayList<Chapter>)
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

    fun getModuleAll(chapter: Chapter, afterResult: (modules: ArrayList<Module>) -> Unit) {
        databaseModules.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val modules = ArrayList<Module>()
                data?.children?.mapNotNullTo(modules, {
                    it.getValue<Module>(Module::class.java)
                })
                afterResult(modules.filter { it.chapterId == chapter.id }.sortedBy { it.position } as ArrayList<Module>)
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

    fun addUserMIQ(userMIQ: UserMIQ): String {
        if (userMIQ.id.isEmpty()) {
            userMIQ.id = databaseUsersMsIsQs.push().key
        }
        databaseUsersMsIsQs.child(userMIQ.id).setValue(userMIQ)
        return userMIQ.id
    }

    fun editUserMIQ(userMIQ: UserMIQ, onCompleteListener: OnCompleteListener<Void> = OnCompleteListener { }) {
        databaseUsersMsIsQs.child(userMIQ.id).setValue(userMIQ).addOnCompleteListener(onCompleteListener)
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

    fun getUserMIQAll(user: User, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                afterResult(usersMsIsQs.filter { it.userId == user.id }.sortedBy { it.position } as ArrayList<UserMIQ>)
            }
        })
    }

    fun getUserMIQAll(module: Module, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                afterResult(usersMsIsQs.filter { it.moduleId == module.id }.sortedBy { it.position } as ArrayList<UserMIQ>)
            }
        })
    }

    fun getUserMIQAll(info: Info, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                afterResult(usersMsIsQs.filter { !it.question && it.infoId == info.id }.sortedBy { it.position } as ArrayList<UserMIQ>)
            }
        })
    }

    fun getUserMIQAll(question: Question, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                afterResult(usersMsIsQs.filter { it.question && it.questionId == question.id }.sortedBy { it.position } as ArrayList<UserMIQ>)
            }
        })
    }

    fun getUserMIQAll(user: User, module: Module, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val usersMsIsQs = ArrayList<UserMIQ>()
                data?.children?.mapNotNullTo(usersMsIsQs, {
                    it.getValue<UserMIQ>(UserMIQ::class.java)
                })
                afterResult(usersMsIsQs.filter { it.userId == user.id && it.moduleId == module.id }.sortedBy { it.position } as ArrayList<UserMIQ>)
            }
        })
    }

    fun getUserMIQForLesson(userId: String, lesson: Lesson, afterResult: (usersMsIsQs: ArrayList<UserMIQ>) -> Unit) {
        databaseChapters.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                val chapters = ArrayList<Chapter>()
                data?.children?.mapNotNullTo(chapters, { it.getValue(Chapter::class.java) })
                chapters.filter { it.lessonId == lesson.id }.sortedBy { it.position }
                databaseModules.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError?) {

                    }

                    override fun onDataChange(data: DataSnapshot?) {
                        val modules = ArrayList<Module>()
                        data?.children?.mapNotNullTo(modules, { it.getValue(Module::class.java) })
                        for (chapter in chapters) {
                            modules.filter { chapter.id != it.chapterId }
                                    .sortedBy { it.position }
                                    .forEach { modules.remove(it) }
                        }
                        databaseUsersMsIsQs.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError?) {

                            }

                            override fun onDataChange(data: DataSnapshot?) {
                                val usersMsIsQs = ArrayList<UserMIQ>()
                                data?.children?.mapNotNullTo(usersMsIsQs, { it.getValue(UserMIQ::class.java) })
                                for (module in modules) {
                                    usersMsIsQs.filter { module.id != it.moduleId || it.userId != userId }
                                            .sortedBy { it.position }
                                            .forEach { usersMsIsQs.remove(it) }
                                }
                                afterResult(usersMsIsQs)
                            }
                        })
                    }
                })
            }
        })
    }

}