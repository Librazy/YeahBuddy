/**
 * 数据仓库
 *
 * @apiNote {@code queryById} 方法用于提供悲观写锁
 * <pre>{@code
 *     @NotNull
 *     @Lock(LockModeType.PESSIMISTIC_WRITE)
 *     Optional<Entity> queryById(ID id);
 * }</pre>
 * @implSpec 所有的仓库 {@link org.springframework.stereotype.Repository} 类都应当组织到这个包
 * @implNote 当前仓库类习惯上继承 {@link org.springframework.data.jpa.repository.JpaRepository}, 可按需调整
 * @see org.springframework.data.repository.Repository
 */
package cn.edu.xmu.yeahbuddy.domain.repo;