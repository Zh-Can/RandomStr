package random;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import cn.hutool.db.Entity;
import cn.hutool.db.SqlRunner;

public class RandomTest2 {

	public static void main(String[] args) throws SQLException {
//		System.out.println(getRandomString(6));
		
		long time1 = System.nanoTime();
		List<Entity> list = new ArrayList<Entity>();
		RedKey redKey = null;
//		HashSet<String> list = new HashSet<String>();
		for (int i = 1; i < 200001; i++) {
			redKey = new RedKey();
			redKey.setKey(encode(i));
			list.add(Entity.parse(redKey));
		}
		long time2 = System.nanoTime();
		System.out.println(list.size());
		System.out.println("生成消耗时间：" + ((double)(time2 - time1)/1000/1000/1000) + "s");
		
		long time3 = System.nanoTime();
		SqlRunner runner = SqlRunner.create();
		int[] result = runner.insert(list);
		long time4 = System.nanoTime();
		System.out.println("存库消耗时间：" + ((double)(time4 - time3)/1000/1000/1000) + "s");
		
//		List<String> duplicateElements = getDuplicateElements(list);
//		System.out.println("list 中重复的元素：" + duplicateElements);
		
//		System.out.println(list);
		
//		922,3372,0368,5477,5807
	}

	// 生成指定位数随机数
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "0123456789abcdefghjklmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";// 生成字符串从此序列中取
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	
	/** 最大值 */
	public static final long MAX = 60466176; // 36 ^ 5

	/** 乘法用的素数 */
	public static final long P = 15485857;

	/** 加法用的素数 */
	public static final long Q = 9007;

	/** 编码长度 */
	public static final int LEN = 5;

	/** 采用36进制 */
	public static final int RADIX = 36;
	/**
	 * 编码方法。
	 * https://bbs.csdn.net/topics/390808317
	 * @param number 序号
	 * @return 5位编码
	 * @throws IllegalArgumentException 如果序号超过范围
	 */
	public static String encode(int number) {
		if (number <= 0 || number > MAX) {
			throw new IllegalArgumentException();
		}
		//P越大，产生的编码越“随机”
		//确保P*MAX小于Long.MAX_VALUE
		long x = ((long) number * P + Q) % MAX;
		char[] codes = new char[LEN];
		Arrays.fill(codes, '0');
		String str = Long.toString(x, RADIX);
		System.arraycopy(str.toCharArray(), 0, codes, LEN - str.length(), str.length());
		reverse(codes);
		return new String(codes).toUpperCase();
	}
	private static void reverse(char[] codes) {
		for (int i = LEN >> 1; i-- > 0;) {
			codes[i] ^= codes[LEN - i - 1];
			codes[LEN - i - 1] ^= codes[i];
			codes[i] ^= codes[LEN - i - 1];
		}
	}

	
	/**
	 * java8 去出重复的值的list 
	 * getDuplicateElements
	 * @author Can
	 * @param list
	 * @return
	 */
	public static List<String> getDuplicateElements(List<String> list) {
		return list.stream() // list 对应的 Stream
				.collect(Collectors.toMap(e -> e, e -> 1, (a, b) -> a + b)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
				.entrySet().stream() // 所有 entry 对应的 Stream
				.filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
				.map(entry -> entry.getKey()) // 获得 entry 的键（重复元素）对应的 Stream
				.collect(Collectors.toList()); // 转化为 List
	}

}
